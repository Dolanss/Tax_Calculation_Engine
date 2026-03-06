package com.taxengine.service;

import com.taxengine.domain.entity.ServiceTaxRule;
import com.taxengine.domain.repository.ServiceTaxRuleRepository;
import com.taxengine.domain.service.TaxCalculationService;
import com.taxengine.dto.TaxCalculationRequest;
import com.taxengine.dto.TaxCalculationResponse;
import com.taxengine.exception.RuleNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaxCalculationService")
class TaxCalculationServiceTest {

    @Mock
    private ServiceTaxRuleRepository taxRuleRepository;

    @InjectMocks
    private TaxCalculationService service;

    private static final String MUNICIPALITY = "3550308";
    private static final String SERVICE_CODE = "1.01";

    private ServiceTaxRule rule(String serviceCode, String municipalityCode, double aliquot) {
        return ServiceTaxRule.builder()
                .id(1L)
                .serviceCode(serviceCode)
                .municipalityCode(municipalityCode)
                .description("Test service")
                .aliquot(BigDecimal.valueOf(aliquot))
                .minAliquot(BigDecimal.valueOf(2.00))
                .maxAliquot(BigDecimal.valueOf(5.00))
                .active(true)
                .build();
    }

    private TaxCalculationRequest request(String municipality, String serviceCode, double grossValue) {
        TaxCalculationRequest req = new TaxCalculationRequest();
        req.setMunicipalityCode(municipality);
        req.setServiceCode(serviceCode);
        req.setGrossValue(BigDecimal.valueOf(grossValue));
        return req;
    }

    @Nested
    @DisplayName("calculate()")
    class Calculate {

        @Test
        @DisplayName("returns correct ISS amount for 2% aliquot")
        void correctIssAmount_2percent() {
            when(taxRuleRepository.findByMunicipalityCodeAndServiceCodeAndActiveTrue(MUNICIPALITY, SERVICE_CODE))
                    .thenReturn(Optional.of(rule(SERVICE_CODE, MUNICIPALITY, 2.00)));

            TaxCalculationResponse result = service.calculate(request(MUNICIPALITY, SERVICE_CODE, 10_000.00));

            assertThat(result.getIssAmount()).isEqualByComparingTo("200.00");
            assertThat(result.getNetValue()).isEqualByComparingTo("9800.00");
            assertThat(result.getGrossValue()).isEqualByComparingTo("10000.00");
            assertThat(result.getAliquot()).isEqualByComparingTo("2.00");
        }

        @Test
        @DisplayName("returns correct ISS amount for 5% aliquot")
        void correctIssAmount_5percent() {
            when(taxRuleRepository.findByMunicipalityCodeAndServiceCodeAndActiveTrue(MUNICIPALITY, "4.01"))
                    .thenReturn(Optional.of(rule("4.01", MUNICIPALITY, 5.00)));

            TaxCalculationResponse result = service.calculate(request(MUNICIPALITY, "4.01", 10_000.00));

            assertThat(result.getIssAmount()).isEqualByComparingTo("500.00");
            assertThat(result.getNetValue()).isEqualByComparingTo("9500.00");
        }

        @ParameterizedTest(name = "grossValue={0}, aliquot={1}% → ISS={2}, net={3}")
        @CsvSource({
                "1000.00,  2.00,  20.00,   980.00",
                "1000.00,  5.00,  50.00,   950.00",
                "1234.56,  3.50,  43.21,  1191.35",
                "0.01,     2.00,   0.00,     0.01",
                "999999.99,5.00,50000.00,949999.99"
        })
        @DisplayName("calculates ISS correctly across aliquot ranges")
        void parameterizedIssCalculation(String gross, String aliquot, String expectedIss, String expectedNet) {
            ServiceTaxRule r = rule(SERVICE_CODE, MUNICIPALITY, Double.parseDouble(aliquot));
            r.setAliquot(new BigDecimal(aliquot));
            when(taxRuleRepository.findByMunicipalityCodeAndServiceCodeAndActiveTrue(MUNICIPALITY, SERVICE_CODE))
                    .thenReturn(Optional.of(r));

            TaxCalculationResponse result = service.calculate(request(MUNICIPALITY, SERVICE_CODE, Double.parseDouble(gross)));

            assertThat(result.getIssAmount()).isEqualByComparingTo(expectedIss);
            assertThat(result.getNetValue()).isEqualByComparingTo(expectedNet);
        }

        @Test
        @DisplayName("populates all response fields")
        void populatesAllFields() {
            when(taxRuleRepository.findByMunicipalityCodeAndServiceCodeAndActiveTrue(MUNICIPALITY, SERVICE_CODE))
                    .thenReturn(Optional.of(rule(SERVICE_CODE, MUNICIPALITY, 2.00)));

            TaxCalculationResponse result = service.calculate(request(MUNICIPALITY, SERVICE_CODE, 5_000.00));

            assertThat(result.getServiceCode()).isEqualTo(SERVICE_CODE);
            assertThat(result.getMunicipalityCode()).isEqualTo(MUNICIPALITY);
            assertThat(result.getServiceDescription()).isEqualTo("Test service");
            assertThat(result.getCalculatedAt()).isNotNull();
        }

        @Test
        @DisplayName("throws RuleNotFoundException when no active rule exists")
        void throwsWhenRuleNotFound() {
            when(taxRuleRepository.findByMunicipalityCodeAndServiceCodeAndActiveTrue(any(), any()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.calculate(request("9999999", "9.99", 1000.00)))
                    .isInstanceOf(RuleNotFoundException.class)
                    .hasMessageContaining("9999999")
                    .hasMessageContaining("9.99");
        }

        @Test
        @DisplayName("delegates to repository with correct parameters")
        void callsRepositoryWithCorrectParams() {
            when(taxRuleRepository.findByMunicipalityCodeAndServiceCodeAndActiveTrue(MUNICIPALITY, SERVICE_CODE))
                    .thenReturn(Optional.of(rule(SERVICE_CODE, MUNICIPALITY, 2.00)));

            service.calculate(request(MUNICIPALITY, SERVICE_CODE, 1_000.00));

            verify(taxRuleRepository, times(1))
                    .findByMunicipalityCodeAndServiceCodeAndActiveTrue(MUNICIPALITY, SERVICE_CODE);
        }
    }

    @Nested
    @DisplayName("findRule()")
    class FindRule {

        @Test
        @DisplayName("returns rule when found in repository")
        void returnsRule() {
            ServiceTaxRule expected = rule(SERVICE_CODE, MUNICIPALITY, 3.00);
            when(taxRuleRepository.findByMunicipalityCodeAndServiceCodeAndActiveTrue(MUNICIPALITY, SERVICE_CODE))
                    .thenReturn(Optional.of(expected));

            ServiceTaxRule result = service.findRule(MUNICIPALITY, SERVICE_CODE);

            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("throws RuleNotFoundException when not found")
        void throwsWhenNotFound() {
            when(taxRuleRepository.findByMunicipalityCodeAndServiceCodeAndActiveTrue(any(), any()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findRule("1111111", "2.02"))
                    .isInstanceOf(RuleNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("rounding behaviour")
    class Rounding {

        @Test
        @DisplayName("rounds ISS half-up to 2 decimal places")
        void roundsHalfUp() {
            // 1234.56 × 3.5% = 43.2096 → rounds to 43.21
            ServiceTaxRule r = rule(SERVICE_CODE, MUNICIPALITY, 3.5);
            r.setAliquot(new BigDecimal("3.50"));
            when(taxRuleRepository.findByMunicipalityCodeAndServiceCodeAndActiveTrue(MUNICIPALITY, SERVICE_CODE))
                    .thenReturn(Optional.of(r));

            TaxCalculationResponse result = service.calculate(request(MUNICIPALITY, SERVICE_CODE, 1234.56));

            assertThat(result.getIssAmount()).isEqualByComparingTo("43.21");
            assertThat(result.getNetValue()).isEqualByComparingTo("1191.35");
        }

        @Test
        @DisplayName("handles minimum invoice value correctly")
        void minimumValue() {
            when(taxRuleRepository.findByMunicipalityCodeAndServiceCodeAndActiveTrue(MUNICIPALITY, SERVICE_CODE))
                    .thenReturn(Optional.of(rule(SERVICE_CODE, MUNICIPALITY, 2.00)));

            TaxCalculationResponse result = service.calculate(request(MUNICIPALITY, SERVICE_CODE, 0.01));

            assertThat(result.getIssAmount()).isEqualByComparingTo("0.00");
            assertThat(result.getNetValue()).isEqualByComparingTo("0.01");
        }
    }
}

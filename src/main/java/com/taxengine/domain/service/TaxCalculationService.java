package com.taxengine.domain.service;

import com.taxengine.domain.entity.ServiceTaxRule;
import com.taxengine.domain.repository.ServiceTaxRuleRepository;
import com.taxengine.dto.TaxCalculationRequest;
import com.taxengine.dto.TaxCalculationResponse;
import com.taxengine.exception.RuleNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaxCalculationService {

    private final ServiceTaxRuleRepository taxRuleRepository;

    /**
     * Calculates ISS tax for a given service and municipality.
     *
     * The tax rule (aliquot) is cached by {municipalityCode}:{serviceCode} with a 1-hour TTL
     * configured in RedisConfig. The gross value is not part of the cache key — the lookup
     * is cheap once the rule is cached, and the arithmetic is applied fresh each time.
     */
    @Transactional(readOnly = true)
    public TaxCalculationResponse calculate(TaxCalculationRequest request) {
        log.info("Calculating ISS for municipality={} serviceCode={} grossValue={}",
                request.getMunicipalityCode(), request.getServiceCode(), request.getGrossValue());

        ServiceTaxRule rule = findRule(request.getMunicipalityCode(), request.getServiceCode());
        return computeTax(rule, request.getGrossValue(), false);
    }

    @Cacheable(value = "taxRules", key = "#municipalityCode + ':' + #serviceCode")
    public ServiceTaxRule findRule(String municipalityCode, String serviceCode) {
        log.debug("Cache miss — fetching rule from DB: municipality={} service={}", municipalityCode, serviceCode);
        return taxRuleRepository
                .findByMunicipalityCodeAndServiceCodeAndActiveTrue(municipalityCode, serviceCode)
                .orElseThrow(() -> new RuleNotFoundException(municipalityCode, serviceCode));
    }

    private TaxCalculationResponse computeTax(ServiceTaxRule rule, BigDecimal grossValue, boolean cached) {
        // ISS = grossValue × (aliquot / 100), rounded to 2 decimal places (HALF_UP per Brazilian standard)
        BigDecimal issAmount = grossValue
                .multiply(rule.getAliquot())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal netValue = grossValue.subtract(issAmount);

        return TaxCalculationResponse.builder()
                .serviceCode(rule.getServiceCode())
                .municipalityCode(rule.getMunicipalityCode())
                .serviceDescription(rule.getDescription())
                .aliquot(rule.getAliquot())
                .grossValue(grossValue)
                .issAmount(issAmount)
                .netValue(netValue)
                .cachedResult(cached)
                .calculatedAt(LocalDateTime.now())
                .build();
    }
}

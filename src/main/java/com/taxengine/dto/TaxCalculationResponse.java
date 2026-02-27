package com.taxengine.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "ISS tax calculation result")
public class TaxCalculationResponse {

    @Schema(description = "ISS service code", example = "1.01")
    private String serviceCode;

    @Schema(description = "IBGE municipality code", example = "3550308")
    private String municipalityCode;

    @Schema(description = "Service description", example = "Análise e desenvolvimento de sistemas")
    private String serviceDescription;

    @Schema(description = "Applied ISS aliquot (%)", example = "2.00")
    private BigDecimal aliquot;

    @Schema(description = "Gross value in BRL", example = "10000.00")
    private BigDecimal grossValue;

    @Schema(description = "Calculated ISS tax amount in BRL", example = "200.00")
    private BigDecimal issAmount;

    @Schema(description = "Net value after ISS deduction in BRL", example = "9800.00")
    private BigDecimal netValue;

    @Schema(description = "Whether this result was served from cache")
    private boolean cachedResult;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Calculation timestamp")
    private LocalDateTime calculatedAt;
}

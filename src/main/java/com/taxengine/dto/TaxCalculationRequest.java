package com.taxengine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "ISS tax calculation request")
public class TaxCalculationRequest {

    @NotBlank(message = "Service code is required")
    @Pattern(regexp = "^\\d{1,2}\\.\\d{2}$", message = "Service code must follow LC 116/2003 format (e.g. 1.01)")
    @Schema(description = "ISS service code per LC 116/2003", example = "1.01")
    private String serviceCode;

    @NotBlank(message = "Municipality code is required")
    @Pattern(regexp = "^\\d{7}$", message = "Municipality code must be 7-digit IBGE code")
    @Schema(description = "IBGE municipality code", example = "3550308")
    private String municipalityCode;

    @NotNull(message = "Gross value is required")
    @DecimalMin(value = "0.01", message = "Gross value must be greater than zero")
    @Digits(integer = 15, fraction = 2, message = "Gross value must have at most 2 decimal places")
    @Schema(description = "Service gross value in BRL", example = "10000.00")
    private BigDecimal grossValue;
}

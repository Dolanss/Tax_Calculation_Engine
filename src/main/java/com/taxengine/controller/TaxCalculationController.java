package com.taxengine.controller;

import com.taxengine.dto.ErrorResponse;
import com.taxengine.dto.TaxCalculationRequest;
import com.taxengine.dto.TaxCalculationResponse;
import com.taxengine.domain.service.TaxCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Tax Calculation", description = "ISS municipal tax calculation")
public class TaxCalculationController {

    private final TaxCalculationService taxCalculationService;

    @PostMapping("/calculate")
    @Operation(
            summary = "Calculate ISS tax",
            description = "Calculates the ISS tax for a given service code and municipality. " +
                          "Tax rules are looked up from PostgreSQL and cached in Redis for 1 hour. " +
                          "ISS = grossValue × aliquot / 100 (HALF_UP rounding, 2 decimal places)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Calculation successful",
                    content = @Content(schema = @Schema(implementation = TaxCalculationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No active rule for municipality + service code",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 404,
                                      "error": "RULE_NOT_FOUND",
                                      "message": "No active ISS rule found for municipality '9999999' and service code '9.99'"
                                    }""")))
    })
    public ResponseEntity<TaxCalculationResponse> calculate(
            @Valid @RequestBody TaxCalculationRequest request) {
        return ResponseEntity.ok(taxCalculationService.calculate(request));
    }
}

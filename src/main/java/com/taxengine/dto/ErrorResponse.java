package com.taxengine.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Error response envelope")
public class ErrorResponse {

    @Schema(example = "400")
    private int status;

    @Schema(example = "VALIDATION_ERROR")
    private String error;

    @Schema(example = "Request validation failed")
    private String message;

    private List<String> details;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(example = "/api/v1/calculate")
    private String path;
}

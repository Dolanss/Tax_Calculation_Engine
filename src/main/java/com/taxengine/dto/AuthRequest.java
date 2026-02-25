package com.taxengine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Authentication credentials")
public class AuthRequest {

    @NotBlank(message = "Username is required")
    @Schema(example = "admin")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(example = "Admin@123")
    private String password;
}

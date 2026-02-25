package com.taxengine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "JWT authentication response")
public class AuthResponse {

    @Schema(description = "JWT access token")
    private String token;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType;

    @Schema(description = "Token expiration in milliseconds")
    private long expiresIn;

    @Schema(description = "Authenticated username")
    private String username;

    @Schema(description = "User role")
    private String role;
}

package com.adregamdi.core.oauth2.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String socialType,
        String oauthAccessToken,
        String authorizationCode,
        String idToken
) {
}

package com.adregamdi.core.oauth2.dto;

public record LoginRequest(
        String oauthAccessToken,
        String socialType
) {
}

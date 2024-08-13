package com.adregamdi.core.oauth2.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}

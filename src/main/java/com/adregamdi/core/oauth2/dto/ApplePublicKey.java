package com.adregamdi.core.oauth2.dto;

public record ApplePublicKey(
        String kty,
        String kid,
        String alg,
        String n,
        String e
) {
}

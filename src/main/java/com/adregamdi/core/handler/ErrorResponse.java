package com.adregamdi.core.handler;

public record ErrorResponse(
        Object statusCode,
        Object error
) {
}
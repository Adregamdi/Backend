package com.adregamdi.core.handler;

import lombok.Builder;

@Builder
public record ApiResponse<T>(
        Object statusCode,
        T data
) {
}

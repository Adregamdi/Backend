package com.adregamdi.shorts.dto.response;

import com.adregamdi.shorts.dto.ShortsDTO;
import lombok.Builder;

@Builder
public record GetShortsByShortsIdResponse(
        ShortsDTO shorts
) {
    public static GetShortsByShortsIdResponse from(final ShortsDTO shorts) {
        return GetShortsByShortsIdResponse.builder()
                .shorts(shorts)
                .build();
    }
}

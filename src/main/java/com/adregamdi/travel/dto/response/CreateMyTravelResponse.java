package com.adregamdi.travel.dto.response;

import lombok.Builder;

@Builder
public record CreateMyTravelResponse(
        Long travelId
) {
    public static CreateMyTravelResponse from(final Long travelId) {
        return CreateMyTravelResponse.builder()
                .travelId(travelId)
                .build();
    }
}

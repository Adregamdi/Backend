package com.adregamdi.travelogue.dto.response;

import lombok.Builder;

@Builder
public record CreateMyTravelogueResponse(
        Long travelogueId
) {
    public static CreateMyTravelogueResponse from(final Long travelogueId) {
        return CreateMyTravelogueResponse.builder()
                .travelogueId(travelogueId)
                .build();
    }
}

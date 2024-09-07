package com.adregamdi.place.dto.response;

import lombok.Builder;

@Builder
public record GetSelectionBasedRecommendationPlacesResponse(
        Long placeId,
        String title,
        String contentsLabel,
        String regionLabel
) {
    public static GetSelectionBasedRecommendationPlacesResponse of() {
        return GetSelectionBasedRecommendationPlacesResponse.builder()
                .build();
    }
}

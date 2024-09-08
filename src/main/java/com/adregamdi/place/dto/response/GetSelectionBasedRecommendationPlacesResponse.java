package com.adregamdi.place.dto.response;

import com.adregamdi.place.domain.Place;
import lombok.Builder;

@Builder
public record GetSelectionBasedRecommendationPlacesResponse(
        Long placeId,
        String title,
        String contentsLabel,
        String regionLabel,
        int reviewCount
) {
    public static GetSelectionBasedRecommendationPlacesResponse of(Place place, int reviewCount) {
        return GetSelectionBasedRecommendationPlacesResponse.builder()
                .placeId(place.getPlaceId())
                .title(place.getTitle())
                .contentsLabel(place.getContentsLabel())
                .regionLabel(place.getRegionLabel())
                .reviewCount(reviewCount)
                .build();
    }
}

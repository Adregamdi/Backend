package com.adregamdi.place.dto.response;

import com.adregamdi.place.domain.Place;
import lombok.Builder;

@Builder
public record GetPlaceResponse(
        boolean isLiked,
        Place place
) {
    public static GetPlaceResponse of(final boolean isLiked, final Place place) {
        return GetPlaceResponse.builder()
                .isLiked(isLiked)
                .place(place)
                .build();
    }
}

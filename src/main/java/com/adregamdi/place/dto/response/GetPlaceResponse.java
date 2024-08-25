package com.adregamdi.place.dto.response;

import com.adregamdi.place.domain.Place;
import lombok.Builder;

@Builder
public record GetPlaceResponse(
        Place place
) {
    public static GetPlaceResponse from(final Place place) {
        return GetPlaceResponse.builder()
                .place(place)
                .build();
    }
}

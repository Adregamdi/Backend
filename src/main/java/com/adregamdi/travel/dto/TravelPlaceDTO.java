package com.adregamdi.travel.dto;

import com.adregamdi.place.domain.Place;
import com.adregamdi.travel.domain.TravelPlace;
import lombok.Builder;

@Builder
public record TravelPlaceDTO(
        TravelPlace travelPlace,
        Place place
) {
    public static TravelPlaceDTO of(TravelPlace travelPlace, Place place) {
        return TravelPlaceDTO.builder()
                .travelPlace(travelPlace)
                .place(place)
                .build();
    }
}

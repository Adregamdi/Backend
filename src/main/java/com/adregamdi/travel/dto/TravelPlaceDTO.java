package com.adregamdi.travel.dto;

import com.adregamdi.place.domain.Place;
import com.adregamdi.place.dto.PlaceReviewDTO;
import com.adregamdi.travel.domain.TravelPlace;
import lombok.Builder;

@Builder
public record TravelPlaceDTO(
        PlaceReviewDTO placeReview,
        TravelPlace travelPlace,
        Place place
) {
    public static TravelPlaceDTO of(PlaceReviewDTO placeReview, TravelPlace travelPlace, Place place) {
        return TravelPlaceDTO.builder()
                .placeReview(placeReview)
                .travelPlace(travelPlace)
                .place(place)
                .build();
    }
}

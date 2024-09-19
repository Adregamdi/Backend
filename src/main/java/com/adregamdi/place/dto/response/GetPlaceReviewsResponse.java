package com.adregamdi.place.dto.response;

import com.adregamdi.place.dto.PlaceReviewDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetPlaceReviewsResponse(
        List<PlaceReviewDTO> reviewList
) {
    public static GetPlaceReviewsResponse from(List<PlaceReviewDTO> placeReviewDTOS) {
        return GetPlaceReviewsResponse.builder()
                .reviewList(placeReviewDTOS)
                .build();
    }
}

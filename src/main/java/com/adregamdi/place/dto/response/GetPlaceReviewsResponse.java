package com.adregamdi.place.dto.response;

import com.adregamdi.place.dto.PlaceReviewDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetPlaceReviewsResponse(
        List<PlaceReviewDTO> review_list
) {
    public static GetPlaceReviewsResponse from(List<PlaceReviewDTO> placeReviewDTOS) {
        return GetPlaceReviewsResponse.builder()
                .review_list(placeReviewDTOS)
                .build();
    }
}

package com.adregamdi.place.dto.response;

import com.adregamdi.place.dto.MyPlaceReviewDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetMyPlaceReviewResponse(
        List<MyPlaceReviewDTO> review_list
) {
    public static GetMyPlaceReviewResponse from(final List<MyPlaceReviewDTO> myPlaceReviewDTOs) {
        return GetMyPlaceReviewResponse.builder()
                .review_list(myPlaceReviewDTOs)
                .build();
    }
}

package com.adregamdi.place.dto.response;

import com.adregamdi.place.domain.PlaceReviewImage;

import java.time.LocalDate;
import java.util.List;

public record GetPlaceReviewResponse(
        Long placeReviewId,
        String title,
        String contentsLabel,
        String regionLabel,
        String visitDate,
        String content,
        List<PlaceReviewImage> placeReviewImageList,
        LocalDate createdAt
) {
}

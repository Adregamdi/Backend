package com.adregamdi.place.dto;

import com.adregamdi.place.domain.PlaceReviewImage;

import java.time.LocalDate;
import java.util.List;

public record MyPlaceReviewDTO(
        String title,
        String contentsLabel,
        String regionLabel,
        Integer imageReviewCount,
        Integer shortsReviewCount,
        String travelDate,
        String content,
        List<PlaceReviewImage> placeReviewImageList,
        LocalDate createdAt
) {
}

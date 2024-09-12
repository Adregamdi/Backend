package com.adregamdi.place.dto;

import com.adregamdi.place.domain.Place;

import java.util.List;

public record PopularPlaceDTO(
        Place place,
        Long photoReviewCount,
        Long shortsCount,
        List<String> imageUrls
) {
}

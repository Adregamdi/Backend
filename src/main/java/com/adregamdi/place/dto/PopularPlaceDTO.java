package com.adregamdi.place.dto;

import com.adregamdi.place.domain.Place;

public record PopularPlaceDTO(
        Place place,
        Long photoReviewCount,
        Long shortsCount
) {
}

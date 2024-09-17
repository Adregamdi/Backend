package com.adregamdi.search.dto;

import java.util.List;

public record PlaceSearchDTO(
        Long placeId,
        String title,
        String contentsLabel,
        String regionLabel,
        String roadAddress,
        Long photoReviewCount,
        Long shortsCount,
        List<String> imageUrls
) {
}

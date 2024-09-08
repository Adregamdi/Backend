package com.adregamdi.search.dto;

import java.util.List;

public record PlaceSearchDTO(
        Long placeId,
        String title,
        String contentsLabel,
        String regionLabel,
        List<String> imageUrls,
        Long photoReviewCount,
        Long shortsCount
) {
}

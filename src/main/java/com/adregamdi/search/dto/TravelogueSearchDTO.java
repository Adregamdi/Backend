package com.adregamdi.search.dto;

import java.util.List;

public record TravelogueSearchDTO(
        Long travelogueId,
        String title,
        String profile,
        String memberHandle,
        List<String> imageUrls
) {
}

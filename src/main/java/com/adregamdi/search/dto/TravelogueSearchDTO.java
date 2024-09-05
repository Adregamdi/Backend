package com.adregamdi.search.dto;

import java.util.List;

public record TravelogueSearchDTO(
        Long travelogueId,
        String title,
        String memberHandle,

        List<String> imageUrls
) {
}

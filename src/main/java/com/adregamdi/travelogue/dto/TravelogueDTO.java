package com.adregamdi.travelogue.dto;

import java.util.List;

public record TravelogueDTO(
        Long travelogueId,
        String title,
        String memberHandle,
        List<String> imageUrls
) {
}

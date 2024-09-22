package com.adregamdi.travelogue.dto;

import java.util.List;

public record HotTravelogueDTO(
        Long travelogueId,
        String title,
        String memberId,
        String memberHandle,
        String profile,
        int likeCount,
        List<String> imageUrls
) {

}

package com.adregamdi.search.dto;

import java.util.UUID;

public record ShortsSearchDTO(
        Long shortsId,
        String title,
        UUID memberId,
        String name,
        String handle,
        String profile,
        Long placeId,
        String placeTitle,
        Long travelogueId,
        String travelogueTitle,
        String shortsVideoUrl,
        String thumbnailUrl,
        int viewCount,
        int likeCount,
        boolean isLiked
) {
}

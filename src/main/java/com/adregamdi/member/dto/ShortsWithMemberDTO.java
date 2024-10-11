package com.adregamdi.member.dto;

public record ShortsWithMemberDTO(
        Long shortsId,
        String title,
        String memberId,
        Long placeId,
        Long travelogueId,
        String shortsVideoUrl,
        String thumbnailUrl,
        boolean assignedStatus,
        int viewCount,
        String memberName,
        String memberHandle,
        String memberProfile
) {
}

package com.adregamdi.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShortsContentDTO {

    private Long shortsId;
    private String title;
    private String memberId;
    private String name;
    private String handle;
    private String profile;
    private Long placeId;
    private String placeTitle;
    private Long travelogueId;
    private String travelogueTitle;
    private String shortsVideoUrl;
    private String thumbnailUrl;
    private int viewCount;
    private int likeCount;
    private Boolean isLiked;

    public ShortsContentDTO(Long shortsId, String title, String memberId, String name, String handle, String profile, Long placeId, String placeTitle, Long travelogueId, String travelogueTitle, String shortsVideoUrl, String thumbnailUrl, Integer viewCount, Integer likeCount, Boolean isLiked) {
        this.shortsId = shortsId;
        this.title = title;
        this.memberId = memberId;
        this.name = name;
        this.handle = handle;
        this.profile = profile;
        this.placeId = placeId;
        this.placeTitle = placeTitle;
        this.travelogueId = travelogueId;
        this.travelogueTitle = travelogueTitle;
        this.shortsVideoUrl = shortsVideoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.viewCount = viewCount == null ? 0 : viewCount;
        this.likeCount = likeCount == null ? 0 : likeCount;
        this.isLiked = isLiked;
    }
}
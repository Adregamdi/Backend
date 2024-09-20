package com.adregamdi.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShortsContentDTO {

    private Long shortsId;
    private String title;
    private UUID memberId;
    private Long placeId;
    private String placeTitle;
    private Long travelogueId;
    private String travelogueTitle;
    private String shortsVideoUrl;
    private String thumbnailUrl;
    private int viewCount;
    private Boolean isLiked;
}
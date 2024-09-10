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
    private Long placeId;
    private String placeName; // 장소 이름
    private Long travelReviewId;
    private String travelTitle;
    private String shortsVideoUrl;
    private String thumbnailUrl;
    private int viewCount;
    private boolean isLiked;

}
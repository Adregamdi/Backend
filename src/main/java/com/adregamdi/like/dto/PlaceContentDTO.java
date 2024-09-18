package com.adregamdi.like.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PlaceContentDTO{

    private Long placeId;
    private String title; // 장소 이름
    private String regionLabel; // 지역 라벨
    private String region1Cd; // 1차 지역 코드
    private String region2Cd; // 2차 지역 코드
    private String imgPath; // 일반 이미지 경로
    private String thumbnailPath; // 썸네일 이미지 경로
    private List<String> imageList; // imageReview에서 가져온 이미지 목록
    private long imageReviewCnt; // 관련 장소 이미지 리뷰 개수
    private long shortsReviewCnt; // 관련 장소 쇼츠 개수

    public PlaceContentDTO(Long placeId, String title, String regionLabel, String region1Cd, String region2Cd, String imgPath, String thumbnailPath, List<String> imageList, Integer imageReviewCnt, Integer shortsReviewCnt) {
        this.placeId = placeId;
        this.title = title;
        this.regionLabel = regionLabel;
        this.region1Cd = region1Cd;
        this.region2Cd = region2Cd;
        this.imgPath = imgPath;
        this.thumbnailPath = thumbnailPath;
        this.imageList = imageList != null ? imageList : new ArrayList<>();
        this.imageReviewCnt = imageReviewCnt != null ? imageReviewCnt : 0;
        this.shortsReviewCnt = shortsReviewCnt != null ? shortsReviewCnt : 0;
    }
}
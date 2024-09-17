package com.adregamdi.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceContentDTO{

    private Long placeId;
    private String title; // 장소 이름
    private String contentsLabel; // 콘텐츠 라벨
    private String regionLabel; // 지역 라벨
    private String region1Cd; // 1차 지역 코드
    private String region2Cd; // 2차 지역 코드
    private String tag; // 태그
    private String imgPath; // 일반 이미지 경로
    private String thumbnailPath; // 썸네일 이미지 경로
    private List<String> imageList; // imageReview에서 가져온 이미지 목록
    private int imageReviewCnt; // 관련 장소 이미지 리뷰 개수
    private int shortsReviewCnt; // 관련 장소 쇼츠 개수

}
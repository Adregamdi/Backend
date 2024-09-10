package com.adregamdi.like.dto;

import com.adregamdi.like.domain.enumtype.ContentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceContentDTO implements LikeContent{

    private Long placeId;
    private String title; // 장소 이름
    private String contentsLabel; // 콘텐츠 라벨
    private String regionLabel; // 지역 라벨
    private String region1Cd; // 1차 지역 코드
    private String region2Cd; // 2차 지역 코드
    private String address; // 주소
    private String roadAddress; // 도로명 주소
    private String tag; // 태그
    private String introduction; // 소개
    private String information; // 정보
    private Double latitude; // 위도
    private Double longitude; // 경도
    private String phoneNo; // 전화번호
    private String imgPath; // 일반 이미지 경로
    private String thumbnailPath; // 썸네일 이미지 경로
//    private List<String> placeImgList; // 장소 리뷰 이미지 리스트 추후 업데이트


    @Override
    public ContentType getContentType() {
        return null;
    }

    @Override
    public Long getContentId() {
        return 0L;
    }

    @Override
    public String getThumbnailUrl() {
        return "";
    }
}
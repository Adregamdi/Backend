package com.adregamdi.place.domain;

import com.adregamdi.core.entity.BaseTime;
import com.adregamdi.place.dto.request.CreatePlaceRequest;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_place")
public class Place extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;
    @Column
    private String title; // 장소 이름
    @Column
    private String contentsLabel; // 콘텐츠 라벨
    @Column
    private String regionLabel; // 지역 라벨
    @Column
    private String region1Cd; // 1차 지역 코드
    @Column
    private String region2Cd; // 2차 지역 코드
    @Column
    private String address; // 주소
    @Column
    private String roadAddress; // 도로명 주소
    @Column
    private String tag; // 태그
    @Column
    private String introduction; // 소개
    @Column
    private String information; // 정보
    @Column
    private Double latitude; // 위도
    @Column
    private Double longitude; // 경도
    @Column
    private String phoneNo; // 전화번호
    @Column
    private String imgPath; // 일반 이미지 경로
    @Column
    private String thumbnailPath; // 썸네일 이미지 경로

    public Place(String title, String contentsLabel, String region1Value, String region2Value, String region2Label, String address, String roadAddress, String tag, String introduction, double latitude, double longitude, String phoneNo, String imgPath, String thumbnailPath) {
        this.title = title;
        this.contentsLabel = contentsLabel;
        this.region1Cd = region1Value;
        this.region2Cd = region2Value;
        this.regionLabel = region2Label;
        this.address = address;
        this.roadAddress = roadAddress;
        this.tag = tag;
        this.introduction = introduction;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNo = phoneNo;
        this.imgPath = imgPath;
        this.thumbnailPath = thumbnailPath;
    }

    public Place(CreatePlaceRequest request) {
        this.title = request.title();
        this.contentsLabel = request.contentsLabel();
        this.region1Cd = request.region1Cd();
        this.region2Cd = request.region2Cd();
        this.regionLabel = request.regionLabel();
        this.address = request.address();
        this.roadAddress = request.roadAddress();
        this.tag = request.tag();
        this.introduction = request.introduction();
        this.information = request.information();
        this.latitude = request.latitude();
        this.longitude = request.longitude();
        this.phoneNo = request.phoneNo();
        this.imgPath = request.imgPath();
        this.thumbnailPath = request.thumbnailPath();
    }
}

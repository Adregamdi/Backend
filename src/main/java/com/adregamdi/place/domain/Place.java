package com.adregamdi.place.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tbl_place")
public class Place extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;
    @Column
    private String title; // 장소 이름
    @Column
    private String contentsId; // 콘텐츠 id
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
    @Column(length = 500)
    private String tag; // 태그
    @Column(length = 1000)
    private String introduction; // 소개
    @Column(length = 1000)
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
    @Column
    private Integer addCount; // 장소 카운트

    @Builder
    public Place(String title, String contentsId, String contentsLabel, String regionLabel,
                 String region1Cd, String region2Cd, String address, String roadAddress,
                 String tag, String introduction, String information, Double latitude,
                 Double longitude, String phoneNo, String imgPath, String thumbnailPath
    ) {
        this.title = title;
        this.contentsId = contentsId;
        this.contentsLabel = contentsLabel;
        this.regionLabel = regionLabel;
        this.region1Cd = region1Cd;
        this.region2Cd = region2Cd;
        this.address = address;
        this.roadAddress = roadAddress;
        this.tag = tag;
        this.introduction = introduction;
        this.information = information;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNo = phoneNo;
        this.imgPath = imgPath;
        this.thumbnailPath = thumbnailPath;
        this.addCount = 0;
    }

    public void updateAddCount(int addCount) {
        this.addCount = addCount;
    }
}

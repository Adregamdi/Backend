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

    public Place(CreatePlaceRequest request) {
        this.title = request.title();
        this.information = request.information();
        this.latitude = request.latitude();
        this.longitude = request.longitude();
        this.imgPath = request.imgPath();
    }
}

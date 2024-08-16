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
    private Long id;
    @Column
    private String name; // 장소 이름
    @Column
    private String information; // 기본 정보
    @Column
    private String image; // 장소 사진
    @Column
    private Double latitude; // 위도
    @Column
    private Double longitude; // 경도
    @Column
    private Integer locationNo; // 지역번호

    public Place(CreatePlaceRequest request) {
        this.name = request.name();
        this.information = request.information();
        this.image = request.image();
        this.latitude = request.latitude();
        this.longitude = request.longitude();
        this.locationNo = request.locationNo();
    }
}

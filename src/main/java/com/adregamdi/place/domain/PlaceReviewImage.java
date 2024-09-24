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
@Table(name = "tbl_place_review_image")
public class PlaceReviewImage extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeReviewImageId;
    @Column
    private Long placeReviewId; // 장소 리뷰 id
    @Column
    private String url; // 이미지 url

    @Builder
    public PlaceReviewImage(Long placeReviewId, String url) {
        this.placeReviewId = placeReviewId;
        this.url = url;
    }
}

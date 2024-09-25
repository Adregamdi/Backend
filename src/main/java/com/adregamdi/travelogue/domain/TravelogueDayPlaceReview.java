package com.adregamdi.travelogue.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tbl_travelogue_day_place_review")
public class TravelogueDayPlaceReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long travelogueDayId;
    @Column(nullable = false)
    private Long placeId;
    @Column
    private Long placeReviewId;

    @Builder
    public TravelogueDayPlaceReview(Long travelogueDayId, Long placeId, Long placeReviewId) {
        this.travelogueDayId = travelogueDayId;
        this.placeId = placeId;
        this.placeReviewId = placeReviewId;
    }
}

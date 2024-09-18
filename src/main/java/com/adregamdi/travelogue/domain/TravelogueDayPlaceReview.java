package com.adregamdi.travelogue.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_travelogue_day_place_review")
public class TravelogueDayPlaceReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long travelogueDayId;

    @Column(nullable = false, unique = true)
    private Long placeReviewId;

    public TravelogueDayPlaceReview(Long travelogueDayId, Long placeReviewId) {
        this.travelogueDayId = travelogueDayId;
        this.placeReviewId = placeReviewId;
    }
}

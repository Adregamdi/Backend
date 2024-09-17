package com.adregamdi.place.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_place_review")
public class PlaceReview extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeReviewId;
    @Column
    private UUID memberId; // 회원 id
    @Column
    private Long placeId; // 장소 id
    @Column
    private Long travelogueId; // 여행기 id
    @Column
    private Long travelogueDayId; // 날짜 별 여행기 id
    @Column
    private String content; // 내용

    public PlaceReview(String memberId, Long placeId, Long travelogueId, Long travelogueDayId, String content) {
        this.memberId = UUID.fromString(memberId);
        this.placeId = placeId;
        this.travelogueId = travelogueId;
        this.travelogueDayId = travelogueDayId;
        this.content = content;
    }

    public PlaceReview(String memberId, Long placeId, String content) {
        this.memberId = UUID.fromString(memberId);
        this.placeId = placeId;
        this.content = content;
    }
}

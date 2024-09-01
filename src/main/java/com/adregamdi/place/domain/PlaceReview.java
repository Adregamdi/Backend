package com.adregamdi.place.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

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
    private String memberId; // 회원 id
    @Column
    private Long placeId; // 장소 id
    @Column
    private String content; // 내용

    public PlaceReview(String memberId, Long placeId, String content) {
        this.memberId = memberId;
        this.placeId = placeId;
        this.content = content;
    }
}

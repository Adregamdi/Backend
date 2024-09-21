package com.adregamdi.place.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tbl_place_review")
public class PlaceReview extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeReviewId;
    @Column(name = "member_id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String memberId;  // 회원 id
    @Column
    private Long placeId; // 장소 id
    @Column
    private LocalDate visitDate; // 방문 날짜
    @Column
    private String content; // 내용

    @Builder
    public PlaceReview(String memberId, Long placeId, LocalDate visitDate, String content) {
        this.memberId = memberId;
        this.placeId = placeId;
        this.visitDate = visitDate;
        this.content = content;
    }
}

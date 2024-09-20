package com.adregamdi.place.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
    @Column(name = "member_id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private UUID memberId; // 회원 id
    @Column
    private Long placeId; // 장소 id
    @Column
    private LocalDate visitDate; // 방문 날짜
    @Column
    private String content; // 내용

    public PlaceReview(String memberId, Long placeId, LocalDate visitDate, String content) {
        this.memberId = UUID.fromString(memberId);
        this.placeId = placeId;
        this.visitDate = visitDate;
        this.content = content;
    }
}

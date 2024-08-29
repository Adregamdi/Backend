package com.adregamdi.travel.domain;

import com.adregamdi.core.entity.BaseTime;
import com.adregamdi.travel.dto.request.CreateMyTravelRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_travel")
public class Travel extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelId;
    @Column
    private String memberId; // 회원 id
    @Column
    private LocalDate startDate; // 시작일
    @Column
    private LocalDate endDate; // 종료일
    @Column
    private String title; // 제목
    @Column
    private Integer day; // 해당 날짜
    @Column
    private String memo; // 메모

    public Travel(CreateMyTravelRequest request, String memberId) {
        this.memberId = memberId;
        this.startDate = request.startDate();
        this.endDate = request.endDate();
        this.title = request.title();
        this.day = request.day();
        this.memo = request.memo();
    }

    public void updateTravel(CreateMyTravelRequest request) {
        this.startDate = request.startDate();
        this.endDate = request.endDate();
        this.title = request.title();
        this.day = request.day();
        this.memo = request.memo();
    }
}

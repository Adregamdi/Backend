package com.adregamdi.travel.domain;

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
@Table(name = "tbl_travel")
public class Travel extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelId;
    @Column(name = "member_id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String memberId;  // 회원 id
    @Column
    private LocalDate startDate; // 시작일
    @Column
    private LocalDate endDate; // 종료일
    @Column
    private String title; // 제목

    @Builder
    public Travel(String memberId, LocalDate startDate, LocalDate endDate, String title) {
        this.memberId = memberId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
    }

    public void update(String memberId, LocalDate startDate, LocalDate endDate, String title) {
        this.memberId = memberId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
    }
}

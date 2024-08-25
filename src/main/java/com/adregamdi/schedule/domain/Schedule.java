package com.adregamdi.schedule.domain;

import com.adregamdi.core.entity.BaseTime;
import com.adregamdi.schedule.dto.request.CreateMyScheduleRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_schedule")
public class Schedule extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;
    @Column
    private String memberId; // 회원 id
    @Column
    private LocalDate startDate; // 시작일
    @Column
    private LocalDate endDate; // 종료일
    @Column
    private String title; // 제목

    public Schedule(CreateMyScheduleRequest request, String memberId) {
        this.memberId = memberId;
        this.startDate = request.startDate();
        this.endDate = request.endDate();
        this.title = request.title();
    }
}

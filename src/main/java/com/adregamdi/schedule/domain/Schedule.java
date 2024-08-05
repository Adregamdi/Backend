package com.adregamdi.schedule.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_schedule")
public class Schedule extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private UUID memberId; // 회원 id
    @Column
    private String startDate; // 시작일
    @Column
    private String endDate; // 종료일
    @Column
    private String title; // 제목
}

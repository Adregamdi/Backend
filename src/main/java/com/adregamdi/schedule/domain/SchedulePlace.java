package com.adregamdi.schedule.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_schedule_place")
public class SchedulePlace extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long scheduleId; // 일정 id
    @Column
    private Long placeId; // 장소 id
    @Column
    private Integer day; // 해당 날짜

    public SchedulePlace(Long scheduleId, Long placeId, int day) {
        this.scheduleId = scheduleId;
        this.placeId = placeId;
        this.day = day;
    }
}

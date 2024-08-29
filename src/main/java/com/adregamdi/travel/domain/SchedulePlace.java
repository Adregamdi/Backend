package com.adregamdi.travel.domain;

import com.adregamdi.core.entity.BaseTime;
import com.adregamdi.travel.dto.ScheduleListDTO;
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
    private Integer placeOrder; // 순서

    public SchedulePlace(Long scheduleId, ScheduleListDTO scheduleListDTO) {
        this.scheduleId = scheduleId;
        this.placeId = scheduleListDTO.getPlaceId();
        this.placeOrder = scheduleListDTO.getPlaceOrder();
    }

    public void updateSchedulePlace(Long scheduleId, ScheduleListDTO scheduleListDTO) {
        this.scheduleId = scheduleId;
        this.placeId = scheduleListDTO.getPlaceId();
        this.placeOrder = scheduleListDTO.getPlaceOrder();
    }
}

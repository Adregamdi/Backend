package com.adregamdi.travel.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_travel_day")
public class TravelDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelDayId;
    @Column
    private Long travelId; // 일정 id
    @Column
    private Integer day; // 해당 날짜
    @Column
    private String memo; // 메모

    public TravelDay(Long travelId, Integer day, String memo) {
        this.travelId = travelId;
        this.day = day;
        this.memo = memo;
    }
}

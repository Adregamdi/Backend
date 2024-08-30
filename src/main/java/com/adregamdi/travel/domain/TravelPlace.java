package com.adregamdi.travel.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_travel_place")
public class TravelPlace extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelPlaceId;
    @Column
    private Long travelDayId; // 날짜 별 일정 id
    @Column
    private Long placeId; // 장소 id
    @Column
    private Integer placeOrder; // 순서


    public TravelPlace(Long travelDayId, Long placeId, Integer placeOrder) {
        this.travelDayId = travelDayId;
        this.placeId = placeId;
        this.placeOrder = placeOrder;
    }
}

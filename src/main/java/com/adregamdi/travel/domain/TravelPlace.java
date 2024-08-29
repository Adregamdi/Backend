package com.adregamdi.travel.domain;

import com.adregamdi.core.entity.BaseTime;
import com.adregamdi.travel.dto.TravelListDTO;
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
    private Long id;
    @Column
    private Long travelId; // 일정 id
    @Column
    private Long placeId; // 장소 id
    @Column
    private Integer placeOrder; // 순서

    public TravelPlace(Long travelId, TravelListDTO travelListDTO) {
        this.travelId = travelId;
        this.placeId = travelListDTO.getPlaceId();
        this.placeOrder = travelListDTO.getPlaceOrder();
    }

    public void updateTravelPlace(Long travelId, TravelListDTO travelListDTO) {
        this.travelId = travelId;
        this.placeId = travelListDTO.getPlaceId();
        this.placeOrder = travelListDTO.getPlaceOrder();
    }
}

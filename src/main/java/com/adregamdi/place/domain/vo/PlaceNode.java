package com.adregamdi.place.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceNode {
    private Long placeId;
    private int order;
    private double latitude;
    private double longitude;
}

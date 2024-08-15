package com.adregamdi.place.dto;

import com.adregamdi.place.domain.Place;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDTO {
    private Long id;
    private String name;
    private String information;
    private String image;
    private Double latitude;
    private Double longitude;
    private Integer locationNo;

    public static PlaceDTO from(Place place) {
        return PlaceDTO.builder()
                .id(place.getId())
                .name(place.getName())
                .information(place.getInformation())
                .image(place.getImage())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .locationNo(place.getLocationNo())
                .build();
    }
}

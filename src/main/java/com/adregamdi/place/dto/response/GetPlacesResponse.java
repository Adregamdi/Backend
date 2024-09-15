package com.adregamdi.place.dto.response;

import com.adregamdi.place.dto.PlaceDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetPlacesResponse(
        List<PlaceDTO> placeList
) {
    public static GetPlacesResponse from(final List<PlaceDTO> companyList) {
        return GetPlacesResponse.builder()
                .placeList(companyList)
                .build();
    }
}

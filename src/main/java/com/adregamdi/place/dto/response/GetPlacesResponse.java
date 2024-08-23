package com.adregamdi.place.dto.response;

import com.adregamdi.place.dto.PlaceDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetPlacesResponse(
        List<PlaceDTO> companyList
) {
    public static GetPlacesResponse from(final List<PlaceDTO> companyList) {
        return GetPlacesResponse.builder()
                .companyList(companyList)
                .build();
    }
}

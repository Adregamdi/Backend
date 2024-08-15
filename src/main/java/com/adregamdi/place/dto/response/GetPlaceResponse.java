package com.adregamdi.place.dto.response;

import com.adregamdi.place.dto.PlaceDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetPlaceResponse(
        List<PlaceDTO> companyList
) {
    public static GetPlaceResponse from(final List<PlaceDTO> companyList) {
        return GetPlaceResponse.builder()
                .companyList(companyList)
                .build();
    }
}

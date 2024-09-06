package com.adregamdi.travel.dto.response;

import com.adregamdi.travel.dto.TravelDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetMyTravelsResponse(
        List<TravelDTO> travels
) {
    public static GetMyTravelsResponse from(List<TravelDTO> content) {
        return GetMyTravelsResponse.builder()
                .travels(content)
                .build();
    }
}

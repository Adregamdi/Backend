package com.adregamdi.travel.dto.response;

import com.adregamdi.travel.domain.Travel;
import com.adregamdi.travel.domain.TravelPlace;
import com.adregamdi.travel.dto.TravelDTO;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Builder
public record GetMyTravelResponse(
        List<TravelDTO> travels
) {
    public static GetMyTravelResponse from(List<Travel> travels, List<List<TravelPlace>> travelPlaces) {
        List<TravelDTO> travelDTOS = IntStream.range(0, travels.size())
                .mapToObj(i -> new TravelDTO(travels.get(i), travelPlaces.get(i)))
                .collect(Collectors.toList());

        return GetMyTravelResponse.builder()
                .travels(travelDTOS)
                .build();
    }
}


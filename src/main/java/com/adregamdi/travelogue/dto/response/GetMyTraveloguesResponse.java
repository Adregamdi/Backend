package com.adregamdi.travelogue.dto.response;

import com.adregamdi.travelogue.dto.TravelogueDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetMyTraveloguesResponse(
        int pageSize,
        int currentPage,
        int elements,
        boolean hasNext,
        List<TravelogueDTO> travelogues
) {
    public static GetMyTraveloguesResponse of(int pageSize, int currentPage, int elements, boolean hasNext, List<TravelogueDTO> content) {
        return GetMyTraveloguesResponse.builder()
                .pageSize(pageSize)
                .currentPage(currentPage)
                .elements(elements)
                .hasNext(hasNext)
                .travelogues(content)
                .build();
    }
}

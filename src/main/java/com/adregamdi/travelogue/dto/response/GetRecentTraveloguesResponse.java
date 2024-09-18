package com.adregamdi.travelogue.dto.response;

import com.adregamdi.travelogue.dto.TravelogueDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetRecentTraveloguesResponse(
        int pageSize,
        int currentPage,
        int elements,
        boolean hasNext,
        List<TravelogueDTO> travelogues
) {
    public static GetRecentTraveloguesResponse of(int pageSize, int currentPage, int elements, boolean hasNext, List<TravelogueDTO> content) {
        return GetRecentTraveloguesResponse.builder()
                .pageSize(pageSize)
                .currentPage(currentPage)
                .elements(elements)
                .hasNext(hasNext)
                .travelogues(content)
                .build();
    }
}

package com.adregamdi.travel.dto.response;

import com.adregamdi.travel.dto.TravelDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetMyTravelsResponse(
        int pageSize,
        int currentPage,
        int elements,
        boolean hasNext,
        List<TravelDTO> travels
) {
    public static GetMyTravelsResponse of(int pageSize, int currentPage, int elements, boolean hasNext, List<TravelDTO> content) {
        return GetMyTravelsResponse.builder()
                .pageSize(pageSize)
                .currentPage(currentPage)
                .elements(elements)
                .hasNext(hasNext)
                .travels(content)
                .build();
    }
}

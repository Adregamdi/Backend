package com.adregamdi.search.dto.response;

import com.adregamdi.search.dto.PlaceSearchDTO;
import com.adregamdi.search.dto.ShortsSearchDTO;
import com.adregamdi.search.dto.TravelogueSearchDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record SearchResponse(
        List<TravelogueSearchDTO> travelogues,
        List<ShortsSearchDTO> shorts,
        List<PlaceSearchDTO> places,
        int currentPage,
        int pageSize,
        boolean hasNextTravelogues,
        boolean hasNextShorts,
        boolean hasNextPlaces,
        long totalTravelogues,
        long totalShorts,
        long totalPlaces
) {
    public static SearchResponse of(
            List<TravelogueSearchDTO> travelogues,
            List<ShortsSearchDTO> shorts,
            List<PlaceSearchDTO> places,
            int currentPage,
            int pageSize,
            boolean hasNextTravelogues,
            boolean hasNextShorts,
            boolean hasNextPlaces,
            long totalTravelogues,
            long totalShorts,
            long totalPlaces
    ) {
        return SearchResponse.builder()
                .travelogues(travelogues)
                .shorts(shorts)
                .places(places)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .hasNextTravelogues(hasNextTravelogues)
                .hasNextShorts(hasNextShorts)
                .hasNextPlaces(hasNextPlaces)
                .totalTravelogues(totalTravelogues)
                .totalShorts(totalShorts)
                .totalPlaces(totalPlaces)
                .build();
    }
}

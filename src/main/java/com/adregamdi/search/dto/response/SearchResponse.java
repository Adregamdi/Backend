package com.adregamdi.search.dto.response;

import com.adregamdi.search.dto.SearchType;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record SearchResponse(
        List<SearchResult> travelogues,
        List<SearchResult> shorts,
        List<SearchResult> places,
        int currentPage,
        int pageSize,
        long totalTravelogues,
        long totalShorts,
        long totalPlaces
) {
    public static SearchResponse of(
            List<SearchResult> travelogues,
            List<SearchResult> shorts,
            List<SearchResult> places,
            int currentPage,
            int pageSize,
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
                .totalTravelogues(totalTravelogues)
                .totalShorts(totalShorts)
                .totalPlaces(totalPlaces)
                .build();
    }

    public long getTotalItems() {
        return totalTravelogues + totalShorts + totalPlaces;
    }

    public List<SearchResult> getAllResults() {
        List<SearchResult> allResults = new ArrayList<>();
        allResults.addAll(travelogues);
        allResults.addAll(shorts);
        allResults.addAll(places);
        return allResults;
    }

    @Builder
    public record SearchResult(
            Long id,
            String title,
            SearchType type,
            String description
    ) {
        public static SearchResult of(Long id, String title, SearchType type, String description) {
            return SearchResult.builder()
                    .id(id)
                    .title(title)
                    .type(type)
                    .description(description)
                    .build();
        }
    }
}

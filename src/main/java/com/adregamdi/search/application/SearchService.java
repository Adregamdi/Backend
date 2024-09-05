package com.adregamdi.search.application;

import com.adregamdi.search.dto.PlaceSearchDTO;
import com.adregamdi.search.dto.SearchType;
import com.adregamdi.search.dto.ShortsSearchDTO;
import com.adregamdi.search.dto.TravelogueSearchDTO;
import com.adregamdi.search.dto.response.SearchResponse;
import com.adregamdi.search.infrastructure.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.adregamdi.core.constant.Constant.LARGE_PAGE_SIZE;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchService {
    private final SearchRepository searchRepository;

    public SearchResponse search(String keyword, int page, Set<SearchType> types) {
        int pageSize = LARGE_PAGE_SIZE;
        List<TravelogueSearchDTO> travelogues = new ArrayList<>();
        List<ShortsSearchDTO> shorts = new ArrayList<>();
        List<PlaceSearchDTO> places = new ArrayList<>();
        Map<SearchType, Long> totalCounts = new EnumMap<>(SearchType.class);

        if (types.contains(SearchType.TRAVELOGUE)) {
            travelogues = searchRepository.searchTravelogues(keyword, page, pageSize);
            totalCounts.put(SearchType.TRAVELOGUE, searchRepository.countTravelogues(keyword));
        }
        if (types.contains(SearchType.SHORTS)) {
            shorts = searchRepository.searchShorts(keyword, page, pageSize);
            totalCounts.put(SearchType.SHORTS, searchRepository.countShorts(keyword));
        }
        if (types.contains(SearchType.PLACE)) {
            places = searchRepository.searchPlaces(keyword, page, pageSize);
            totalCounts.put(SearchType.PLACE, searchRepository.countPlaces(keyword));
        }

        return SearchResponse.of(
                travelogues,
                shorts,
                places,
                page,
                pageSize,
                totalCounts.getOrDefault(SearchType.TRAVELOGUE, 0L),
                totalCounts.getOrDefault(SearchType.SHORTS, 0L),
                totalCounts.getOrDefault(SearchType.PLACE, 0L)
        );
    }
}

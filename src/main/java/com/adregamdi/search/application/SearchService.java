package com.adregamdi.search.application;

import com.adregamdi.search.dto.SearchItemDTO;
import com.adregamdi.search.dto.SearchType;
import com.adregamdi.search.dto.response.SearchResponse;
import com.adregamdi.search.infrastructure.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.adregamdi.core.constant.Constant.LARGE_PAGE_SIZE;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchService {
    private final SearchRepository searchRepository;

    public SearchResponse search(String keyword, int page, Set<SearchType> types) {
        List<SearchItemDTO> searchResults = searchRepository.search(keyword, page, LARGE_PAGE_SIZE, types);
        long total = searchRepository.countTotal(keyword);

        List<SearchResponse.SearchResult> travelogues = new ArrayList<>();
        List<SearchResponse.SearchResult> shorts = new ArrayList<>();
        List<SearchResponse.SearchResult> places = new ArrayList<>();

        for (SearchItemDTO item : searchResults) {
            SearchResponse.SearchResult result = SearchResponse.SearchResult.of(
                    item.getId(), item.getTitle(), item.getType(), item.getDescription()
            );
            switch (item.getType()) {
                case TRAVELOGUE -> travelogues.add(result);
                case SHORTS -> shorts.add(result);
                case PLACE -> places.add(result);
            }
        }

        return SearchResponse.of(
                travelogues,
                shorts,
                places,
                page,
                LARGE_PAGE_SIZE,
                total,
                total,
                total
        );
    }
}

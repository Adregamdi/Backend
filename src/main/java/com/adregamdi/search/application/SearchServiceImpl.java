package com.adregamdi.search.application;

import com.adregamdi.core.constant.ContentType;
import com.adregamdi.search.dto.PlaceSearchDTO;
import com.adregamdi.search.dto.ShortsSearchDTO;
import com.adregamdi.search.dto.TravelogueSearchDTO;
import com.adregamdi.search.dto.response.SearchResponse;
import com.adregamdi.search.infrastructure.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.adregamdi.core.constant.Constant.LARGE_PAGE_SIZE;
import static com.adregamdi.core.utils.PageUtil.generatePageAsc;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService {
    private final SearchRepository searchRepository;
    int pageSize = LARGE_PAGE_SIZE;

    /*
     * [콘텐츠 검색]
     * */
    @Override
    @Transactional(readOnly = true)
    public SearchResponse search(
            final String keyword,
            final int page,
            final Set<ContentType> types,
            final String memberId
    ) {
        Map<ContentType, SearchResult<?>> searchResults = performSearch(keyword, page, types, memberId);
        Map<ContentType, Long> totalCounts = getTotalCounts(keyword, types);

        return createSearchResponse(page, searchResults, totalCounts);
    }

    private Map<ContentType, SearchResult<?>> performSearch(
            final String keyword,
            final int page,
            final Set<ContentType> types,
            final String memberId
    ) {
        Map<ContentType, SearchResult<?>> results = new EnumMap<>(ContentType.class);

        results.put(ContentType.TRAVELOGUE, types.contains(ContentType.TRAVELOGUE)
                ? searchTravelogues(keyword, page)
                : new SearchResult<>(emptySlice(page)));

        results.put(ContentType.SHORTS, types.contains(ContentType.SHORTS)
                ? searchShorts(keyword, page, memberId)
                : new SearchResult<>(emptySlice(page)));

        results.put(ContentType.PLACE, types.contains(ContentType.PLACE)
                ? searchPlaces(keyword, page)
                : new SearchResult<>(emptySlice(page)));

        return results;
    }

    private SearchResult<TravelogueSearchDTO> searchTravelogues(final String keyword, final int page) {
        Slice<TravelogueSearchDTO> slice = searchRepository.searchTravelogues(keyword, generatePageAsc(page, pageSize, "title"));
        return new SearchResult<>(slice);
    }

    private SearchResult<ShortsSearchDTO> searchShorts(final String keyword, final int page, final String memberId) {
        Slice<ShortsSearchDTO> slice = searchRepository.searchShorts(keyword, generatePageAsc(page, pageSize, "title"), memberId);
        return new SearchResult<>(slice);
    }

    private SearchResult<PlaceSearchDTO> searchPlaces(final String keyword, final int page) {
        Slice<PlaceSearchDTO> slice = searchRepository.searchPlaces(keyword, generatePageAsc(page, pageSize, "title"));
        return new SearchResult<>(slice);
    }

    private Map<ContentType, Long> getTotalCounts(final String keyword, final Set<ContentType> types) {
        Map<ContentType, Long> totalCounts = new EnumMap<>(ContentType.class);

        if (types.contains(ContentType.TRAVELOGUE)) {
            totalCounts.put(ContentType.TRAVELOGUE, searchRepository.countTravelogues(keyword));
        }
        if (types.contains(ContentType.SHORTS)) {
            totalCounts.put(ContentType.SHORTS, searchRepository.countShorts(keyword));
        }
        if (types.contains(ContentType.PLACE)) {
            totalCounts.put(ContentType.PLACE, searchRepository.countPlaces(keyword));
        }

        return totalCounts;
    }

    private SearchResponse createSearchResponse(
            final int page,
            final Map<ContentType, SearchResult<?>> searchResults,
            final Map<ContentType, Long> totalCounts
    ) {
        return SearchResponse.of(
                page,
                pageSize,
                searchResults.get(ContentType.TRAVELOGUE).hasNext,
                searchResults.get(ContentType.SHORTS).hasNext,
                searchResults.get(ContentType.PLACE).hasNext,
                totalCounts.getOrDefault(ContentType.TRAVELOGUE, 0L),
                totalCounts.getOrDefault(ContentType.SHORTS, 0L),
                totalCounts.getOrDefault(ContentType.PLACE, 0L),
                (List<TravelogueSearchDTO>) searchResults.get(ContentType.TRAVELOGUE).content,
                (List<ShortsSearchDTO>) searchResults.get(ContentType.SHORTS).content,
                (List<PlaceSearchDTO>) searchResults.get(ContentType.PLACE).content
        );
    }

    private <T> Slice<T> emptySlice(final int page) {
        return new SliceImpl<>(Collections.emptyList(), PageRequest.of(page, pageSize), false);
    }

    private static class SearchResult<T> {
        final List<T> content;
        final boolean hasNext;

        SearchResult(Slice<T> slice) {
            this.content = slice.getContent();
            this.hasNext = slice.hasNext();
        }
    }
}

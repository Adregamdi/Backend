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

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static com.adregamdi.core.constant.Constant.LARGE_PAGE_SIZE;
import static com.adregamdi.core.utils.PageUtil.generatePageAsc;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService {
    private final SearchRepository searchRepository;

    /*
     * [콘텐츠 검색]
     * */
    @Transactional(readOnly = true)
    public SearchResponse search(
            final String keyword,
            final int page,
            final Set<ContentType> types,
            final String memberId
    ) {
        int pageSize = LARGE_PAGE_SIZE;
        Slice<TravelogueSearchDTO> travelogues = emptySlice(page, pageSize);
        Slice<ShortsSearchDTO> shorts = emptySlice(page, pageSize);
        Slice<PlaceSearchDTO> places = emptySlice(page, pageSize);
        Map<ContentType, Long> totalCounts = new EnumMap<>(ContentType.class);

        if (types.contains(ContentType.TRAVELOGUE)) {
            travelogues = searchRepository.searchTravelogues(keyword, generatePageAsc(page, pageSize, "title"));
            totalCounts.put(ContentType.TRAVELOGUE, searchRepository.countTravelogues(keyword));
        }
        if (types.contains(ContentType.SHORTS)) {
            shorts = searchRepository.searchShorts(keyword, generatePageAsc(page, pageSize, "title"), memberId);
            totalCounts.put(ContentType.SHORTS, searchRepository.countShorts(keyword));
        }
        if (types.contains(ContentType.PLACE)) {
            places = searchRepository.searchPlaces(keyword, generatePageAsc(page, pageSize, "title"));
            totalCounts.put(ContentType.PLACE, searchRepository.countPlaces(keyword));
        }

        return SearchResponse.of(
                page,
                pageSize,
                travelogues.hasNext(),
                shorts.hasNext(),
                places.hasNext(),
                totalCounts.getOrDefault(ContentType.TRAVELOGUE, 0L),
                totalCounts.getOrDefault(ContentType.SHORTS, 0L),
                totalCounts.getOrDefault(ContentType.PLACE, 0L),
                travelogues.getContent(),
                shorts.getContent(),
                places.getContent()
        );
    }

    private <T> Slice<T> emptySlice(final int page, final int pageSize) {
        return new SliceImpl<>(Collections.emptyList(), PageRequest.of(page, pageSize), false);
    }
}

package com.adregamdi.search.infrastructure;

import com.adregamdi.search.dto.PlaceSearchDTO;
import com.adregamdi.search.dto.ShortsSearchDTO;
import com.adregamdi.search.dto.TravelogueSearchDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface SearchRepository {
    Slice<TravelogueSearchDTO> searchTravelogues(String keyword, Pageable pageable);

    Slice<ShortsSearchDTO> searchShorts(String keyword, Pageable pageable, String memberId);

    Slice<PlaceSearchDTO> searchPlaces(String keyword, Pageable pageable);

    long countTravelogues(String keyword);

    long countShorts(String keyword);

    long countPlaces(String keyword);
}

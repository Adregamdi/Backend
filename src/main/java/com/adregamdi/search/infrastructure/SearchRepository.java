package com.adregamdi.search.infrastructure;

import com.adregamdi.search.dto.PlaceSearchDTO;
import com.adregamdi.search.dto.ShortsSearchDTO;
import com.adregamdi.search.dto.TravelogueSearchDTO;
import org.springframework.data.domain.Slice;

public interface SearchRepository {
    Slice<TravelogueSearchDTO> searchTravelogues(String keyword, int page, int pageSize);

    Slice<ShortsSearchDTO> searchShorts(String keyword, int page, int pageSize);

    Slice<PlaceSearchDTO> searchPlaces(String keyword, int page, int pageSize);

    long countTravelogues(String keyword);

    long countShorts(String keyword);

    long countPlaces(String keyword);
}

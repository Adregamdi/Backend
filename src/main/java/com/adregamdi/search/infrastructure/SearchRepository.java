package com.adregamdi.search.infrastructure;

import com.adregamdi.search.dto.PlaceSearchDTO;
import com.adregamdi.search.dto.ShortsSearchDTO;
import com.adregamdi.search.dto.TravelogueSearchDTO;

import java.util.List;

public interface SearchRepository {
    List<TravelogueSearchDTO> searchTravelogues(String keyword, int page, int pageSize);

    List<ShortsSearchDTO> searchShorts(String keyword, int page, int pageSize);

    List<PlaceSearchDTO> searchPlaces(String keyword, int page, int pageSize);

    long countTravelogues(String keyword);

    long countShorts(String keyword);

    long countPlaces(String keyword);
}

package com.adregamdi.place.application;


import com.adregamdi.place.dto.request.CreatePlaceRequest;
import com.adregamdi.place.dto.response.GetPlaceResponse;
import com.adregamdi.place.dto.response.GetPlacesResponse;
import com.adregamdi.place.dto.response.GetSelectionBasedRecommendationPlacesResponse;

import java.net.URISyntaxException;
import java.util.List;

public interface PlaceService {
    /*
     * [장소 등록]
     * */
    void create(final CreatePlaceRequest request);

    /*
     * [장소 등록 By 외부 API]
     * */
    void createByAPI();

    /*
     * [특정 장소 조회]
     * */
    GetPlaceResponse get(final Long placeId);

    /*
     * [장소 리스트 조회]
     * */
    GetPlacesResponse getPlaces(final int pageNo, final String name);

    /*
     * [선택 기반 추천 장소 리스트 조회]
     * */
    List<GetSelectionBasedRecommendationPlacesResponse> getSelectionBasedRecommendationPlaces(final Double latitude, final Double longitude) throws URISyntaxException;
}

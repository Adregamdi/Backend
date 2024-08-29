package com.adregamdi.place.application;


import com.adregamdi.place.dto.request.CreatePlaceRequest;
import com.adregamdi.place.dto.response.GetPlaceResponse;
import com.adregamdi.place.dto.response.GetPlacesResponse;

public interface PlaceService {

    /*
     * [특정 장소 조회]
     * */
    GetPlaceResponse get(final Long placeId);

    /*
     * [장소 리스트 조회]
     * */
    GetPlacesResponse getPlaces(final int pageNo, final String name);

    /*
     * [장소 등록]
     * */
    void create(final CreatePlaceRequest request);

    /*
     * [장소 등록 By 외부 API]
     * */
    void createByAPI();
}

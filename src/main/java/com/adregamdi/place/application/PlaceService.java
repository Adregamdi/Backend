package com.adregamdi.place.application;


import com.adregamdi.place.dto.response.GetPlaceResponse;

public interface PlaceService {

    /*
     * [특정 장소 조회]
     * */
    GetPlaceResponse get(int pageNo, String name);

    /*
     * [장소 리스트 조회]
     * */
    void getPlaces();
}

package com.adregamdi.travel.application;

import com.adregamdi.travel.dto.request.CreateMyTravelRequest;
import com.adregamdi.travel.dto.response.CreateMyTravelResponse;
import com.adregamdi.travel.dto.response.GetMyTravelResponse;
import com.adregamdi.travel.dto.response.GetMyTravelsResponse;

public interface TravelService {
    /*
     * [일정 등록/수정]
     * */
    CreateMyTravelResponse createMyTravel(String currentMemberId, CreateMyTravelRequest request);

    /*
     * [내 특정 일정 조회]
     * */
    GetMyTravelResponse getMyTravel(String currentMemberId, Long travelId);

    /*
     * [내 전체 일정 조회]
     * */
    GetMyTravelsResponse getMyTravels(String currentMemberId, int page);

    /*
     * [내 특정 일정 삭제]
     * */
    void deleteMyTravel(String currentMemberId, Long travelId);
}

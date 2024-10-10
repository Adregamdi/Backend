package com.adregamdi.travel.application;

import com.adregamdi.travel.dto.request.CreateMyTravelRequest;
import com.adregamdi.travel.dto.response.CreateMyTravelResponse;
import com.adregamdi.travel.dto.response.GetMyTravelResponse;
import com.adregamdi.travel.dto.response.GetMyTravelsResponse;

public interface TravelService {
    CreateMyTravelResponse createMyTravel(final String currentMemberId, final CreateMyTravelRequest request);

    GetMyTravelResponse getMyTravel(final String currentMemberId, final Long travelId);

    GetMyTravelsResponse getMyTravels(final String currentMemberId, final int page);

    void deleteMyTravel(final String currentMemberId, final Long travelId);
}

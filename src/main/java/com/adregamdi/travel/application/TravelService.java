package com.adregamdi.travel.application;

import com.adregamdi.travel.domain.Travel;
import com.adregamdi.travel.domain.TravelDay;
import com.adregamdi.travel.domain.TravelPlace;
import com.adregamdi.travel.dto.TravelListDTO;
import com.adregamdi.travel.dto.request.CreateMyTravelRequest;
import com.adregamdi.travel.dto.response.GetMyTravelResponse;
import com.adregamdi.travel.exception.TravelException.TravelDayNotFoundException;
import com.adregamdi.travel.exception.TravelException.TravelNotFoundException;
import com.adregamdi.travel.exception.TravelException.TravelPlaceNotFoundException;
import com.adregamdi.travel.infrastructure.TravelDayRepository;
import com.adregamdi.travel.infrastructure.TravelPlaceRepository;
import com.adregamdi.travel.infrastructure.TravelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TravelService {
    private final TravelRepository travelRepository;
    private final TravelDayRepository travelDayRepository;
    private final TravelPlaceRepository travelPlaceRepository;

    /*
     * 일정 등록/수정
     * */
    @Transactional
    public void createMyTravel(final CreateMyTravelRequest request, final String memberId) {
        Travel travel = travelRepository.findByMemberIdAndTitleAndDay(memberId, request.title());

        if (travel == null) {
            travel = travelRepository.save(new Travel(request, memberId));
        } else {
            travel.updateTravel(request);
        }

        for (TravelListDTO travelListDTO : request.travelList()) {
            TravelPlace travelPlace = travelPlaceRepository.findByTravelDayIdAndPlaceOrder(travel.getTravelDayId(), travelListDTO.getPlaceOrder());

            if (travelPlace == null) {
                travelPlaceRepository.save(new TravelPlace(travel.getTravelId(), travelListDTO));
            } else {
                travelPlace.updateTravelPlace(travel.getTravelId(), travelListDTO);
            }
        }
    }

    /*
     * 일정 조회
     * */
    @Transactional(readOnly = true)
    public GetMyTravelResponse getMyTravel(final Long travelId, final String memberId) {
        List<TravelPlace> travelPlaces = new ArrayList<>();

        Travel travel = travelRepository.findByTravelIdAndMemberId(travelId, memberId)
                .orElseThrow(() -> new TravelNotFoundException(memberId));

        List<TravelDay> travelDays = travelDayRepository.findByTravelId(travelId)
                .orElseThrow(() -> new TravelDayNotFoundException(travelId));

        for (TravelDay travelDay : travelDays) {
            travelPlaces = travelPlaceRepository.findByTravelDayId(travelDay.getTravelDayId())
                    .orElseThrow(() -> new TravelPlaceNotFoundException(travel.getTravelId()));
        }

        return GetMyTravelResponse.from(travel, travelDays, travelPlaces);
    }
}

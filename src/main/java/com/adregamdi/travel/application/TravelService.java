package com.adregamdi.travel.application;

import com.adregamdi.travel.domain.Travel;
import com.adregamdi.travel.domain.TravelPlace;
import com.adregamdi.travel.dto.TravelListDTO;
import com.adregamdi.travel.dto.request.CreateMyTravelRequest;
import com.adregamdi.travel.dto.request.GetMyTravelRequest;
import com.adregamdi.travel.dto.response.GetMyTravelResponse;
import com.adregamdi.travel.exception.TravelException.TravelNotFoundException;
import com.adregamdi.travel.exception.TravelException.TravelPlaceNotFoundException;
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
    private final TravelPlaceRepository travelPlaceRepository;

    /*
     * 일정 조회
     * */
    @Transactional(readOnly = true)
    public GetMyTravelResponse getMyTravel(final List<GetMyTravelRequest> requests, final String memberId) {
        List<Travel> travels = new ArrayList<>();
        List<List<TravelPlace>> travelPlaces = new ArrayList<>();

        for (GetMyTravelRequest request : requests) {
            Travel travel = travelRepository.findByTravelIdAndMemberId(request.travelId(), memberId)
                    .orElseThrow(TravelNotFoundException::new);
            travels.add(travel);
        }
        for (Travel travel : travels) {
            List<TravelPlace> travelPlace = travelPlaceRepository.findByTravelId(travel.getTravelId())
                    .orElseThrow(() -> new TravelPlaceNotFoundException(travel.getTravelId()));
            travelPlaces.add(travelPlace);
        }
        return GetMyTravelResponse.from(travels, travelPlaces);
    }

    /*
     * 일정 하루 단위로 등록/수정
     * */
    @Transactional
    public void createMyTravel(final CreateMyTravelRequest request, final String memberId) {
        Travel travel = travelRepository.findByMemberIdAndTitleAndDay(memberId, request.title(), request.day());

        if (travel == null) {
            travel = travelRepository.save(new Travel(request, memberId));
        } else {
            travel.updateTravel(request);
        }

        for (TravelListDTO travelListDTO : request.travelList()) {
            TravelPlace travelPlace = travelPlaceRepository.findByTravelIdAndPlaceOrder(travel.getTravelId(), travelListDTO.getPlaceOrder());

            if (travelPlace == null) {
                travelPlaceRepository.save(new TravelPlace(travel.getTravelId(), travelListDTO));
            } else {
                travelPlace.updateTravelPlace(travel.getTravelId(), travelListDTO);
            }
        }
    }
}

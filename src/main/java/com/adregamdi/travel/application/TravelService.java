package com.adregamdi.travel.application;

import com.adregamdi.travel.domain.Travel;
import com.adregamdi.travel.domain.TravelDay;
import com.adregamdi.travel.domain.TravelPlace;
import com.adregamdi.travel.dto.request.CreateMyTravelRequest;
import com.adregamdi.travel.dto.response.GetMyTravelResponse;
import com.adregamdi.travel.exception.TravelException.*;
import com.adregamdi.travel.infrastructure.TravelDayRepository;
import com.adregamdi.travel.infrastructure.TravelPlaceRepository;
import com.adregamdi.travel.infrastructure.TravelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
     * 일정 등록
     * */
    @Transactional
    public void createMyTravel(final CreateMyTravelRequest request, final String memberId) {
        if (request.startDate().isAfter(request.endDate())) {
            throw new InvalidTravelDateException(request);
        }

        Travel travel = travelRepository.save(new Travel(request, memberId));

        for (CreateMyTravelRequest.DayInfo dayInfo : request.dayList()) {
            LocalDate dayDate = request.startDate().plusDays(dayInfo.day() - 1);
            if (dayDate.isAfter(request.endDate())) {
                throw new InvalidTravelDayException(dayInfo.day(), dayInfo);
            }

            TravelDay travelDay = travelDayRepository.save(new TravelDay(travel.getTravelId(), dayInfo.day(), dayInfo.memo()));

            for (CreateMyTravelRequest.PlaceInfo placeInfo : dayInfo.placeList()) {
                travelPlaceRepository.save(new TravelPlace(travelDay.getTravelDayId(), placeInfo.placeId(), placeInfo.placeOrder()));
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

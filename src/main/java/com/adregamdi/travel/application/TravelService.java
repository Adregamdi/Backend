package com.adregamdi.travel.application;

import com.adregamdi.travel.domain.Travel;
import com.adregamdi.travel.domain.TravelDay;
import com.adregamdi.travel.domain.TravelPlace;
import com.adregamdi.travel.dto.TravelDTO;
import com.adregamdi.travel.dto.request.CreateMyTravelRequest;
import com.adregamdi.travel.dto.response.GetMyTravelResponse;
import com.adregamdi.travel.dto.response.GetMyTravelsResponse;
import com.adregamdi.travel.exception.TravelException.*;
import com.adregamdi.travel.infrastructure.TravelDayRepository;
import com.adregamdi.travel.infrastructure.TravelPlaceRepository;
import com.adregamdi.travel.infrastructure.TravelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.adregamdi.core.constant.Constant.LARGE_PAGE_SIZE;
import static com.adregamdi.core.utils.PageUtil.generatePageDesc;

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

            TravelDay travelDay = travelDayRepository.save(new TravelDay(travel.getTravelId(), dayInfo.date(), dayInfo.day(), dayInfo.memo()));

            for (CreateMyTravelRequest.PlaceInfo placeInfo : dayInfo.placeList()) {
                travelPlaceRepository.save(new TravelPlace(travelDay.getTravelDayId(), placeInfo.placeId(), placeInfo.placeOrder()));
            }
        }
    }

    /*
     * 내 특정 일정 조회
     * */
    @Transactional(readOnly = true)
    public GetMyTravelResponse getMyTravel(final Long travelId, final String memberId) {
        List<List<TravelPlace>> travelPlaces = new ArrayList<>();

        Travel travel = travelRepository.findByTravelIdAndMemberId(travelId, UUID.fromString(memberId))
                .orElseThrow(() -> new TravelNotFoundException(memberId));

        List<TravelDay> travelDays = travelDayRepository.findByTravelId(travelId)
                .orElseThrow(() -> new TravelDayNotFoundException(travelId));

        for (TravelDay travelDay : travelDays) {
            List<TravelPlace> travelPlaceList = travelPlaceRepository.findByTravelDayId(travelDay.getTravelDayId())
                    .orElseThrow(() -> new TravelPlaceNotFoundException(travel.getTravelId()));
            travelPlaces.add(travelPlaceList);
        }

        return GetMyTravelResponse.of(travel, travelDays, travelPlaces);
    }

    /*
     * 내 전체 일정 조회
     * */
    @Transactional(readOnly = true)
    public GetMyTravelsResponse getMyTravels(final int page, final String memberId) {
        Slice<TravelDTO> travels = travelRepository.findByMemberId(memberId, generatePageDesc(page, LARGE_PAGE_SIZE, "travelId"));

        return GetMyTravelsResponse.of(
                LARGE_PAGE_SIZE,
                page,
                travels.getNumberOfElements(),
                travels.hasNext(),
                travels.getContent()
        );
    }
}

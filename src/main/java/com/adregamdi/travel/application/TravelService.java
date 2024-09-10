package com.adregamdi.travel.application;

import com.adregamdi.travel.domain.Travel;
import com.adregamdi.travel.domain.TravelDay;
import com.adregamdi.travel.domain.TravelPlace;
import com.adregamdi.travel.dto.TravelDTO;
import com.adregamdi.travel.dto.request.CreateMyTravelRequest;
import com.adregamdi.travel.dto.response.CreateMyTravelResponse;
import com.adregamdi.travel.dto.response.GetMyTravelResponse;
import com.adregamdi.travel.dto.response.GetMyTravelsResponse;
import com.adregamdi.travel.exception.TravelException.InvalidTravelDateException;
import com.adregamdi.travel.exception.TravelException.TravelDayNotFoundException;
import com.adregamdi.travel.exception.TravelException.TravelNotFoundException;
import com.adregamdi.travel.exception.TravelException.TravelPlaceNotFoundException;
import com.adregamdi.travel.infrastructure.TravelDayRepository;
import com.adregamdi.travel.infrastructure.TravelPlaceRepository;
import com.adregamdi.travel.infrastructure.TravelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public CreateMyTravelResponse createMyTravel(final CreateMyTravelRequest request, final String memberId) {
        if (request.startDate().isAfter(request.endDate())) {
            throw new InvalidTravelDateException(request);
        }

        Travel travel;
        if (request.travelId() == null) {
            travel = new Travel(request, memberId);
        } else {
            travel = travelRepository.findById(request.travelId())
                    .orElseThrow(() -> new TravelNotFoundException(request.travelId()));
            travel.update(request);
        }

        travel = travelRepository.save(travel);

        List<TravelDay> existingDays = travelDayRepository.findAllByTravelId(travel.getTravelId());
        Map<Integer, TravelDay> existingDayMap = existingDays.stream()
                .collect(Collectors.toMap(TravelDay::getDay, Function.identity()));

        Set<Integer> processedDays = new HashSet<>();
        long totalDays = ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1;

        for (CreateMyTravelRequest.DayInfo dayInfo : request.dayList()) {
            TravelDay travelDay = existingDayMap.get(dayInfo.day());
            if (travelDay == null) {
                travelDay = new TravelDay(travel.getTravelId(), dayInfo.date(), dayInfo.day(), dayInfo.memo());
            } else {
                travelDay.update(dayInfo.date(), dayInfo.day(), dayInfo.memo());
            }
            travelDay = travelDayRepository.save(travelDay);
            existingDayMap.put(dayInfo.day(), travelDay);
            processedDays.add(dayInfo.day());
        }

        for (int day = 1; day <= totalDays; day++) {
            if (!processedDays.contains(day)) {
                LocalDate date = request.startDate().plusDays(day - 1);
                TravelDay emptyDay = new TravelDay(travel.getTravelId(), date, day, "");
                emptyDay = travelDayRepository.save(emptyDay);
                existingDayMap.put(day, emptyDay);
            }
        }

        for (CreateMyTravelRequest.DayInfo dayInfo : request.dayList()) {
            TravelDay travelDay = existingDayMap.get(dayInfo.day());
            List<TravelPlace> existingPlaces = travelPlaceRepository.findAllByTravelDayId(travelDay.getTravelDayId());
            Map<Integer, TravelPlace> existingPlaceMap = existingPlaces.stream()
                    .collect(Collectors.toMap(TravelPlace::getPlaceOrder, Function.identity()));

            List<TravelPlace> placesToSave = new ArrayList<>();
            for (CreateMyTravelRequest.PlaceInfo placeInfo : dayInfo.placeList()) {
                TravelPlace travelPlace = existingPlaceMap.get(placeInfo.placeOrder());
                if (travelPlace == null) {
                    travelPlace = new TravelPlace(travelDay.getTravelDayId(), placeInfo.placeId(), placeInfo.placeOrder());
                } else {
                    travelPlace.update(placeInfo.placeId(), placeInfo.placeOrder());
                }
                placesToSave.add(travelPlace);
            }
            travelPlaceRepository.saveAll(placesToSave);

            existingPlaces.stream()
                    .filter(place -> dayInfo.placeList().stream().noneMatch(pi -> Objects.equals(pi.placeOrder(), place.getPlaceOrder())))
                    .forEach(travelPlaceRepository::delete);
        }

        List<TravelDay> daysToDelete = existingDays.stream()
                .filter(day -> !processedDays.contains(day.getDay()) && day.getDay() <= totalDays)
                .toList();

        for (TravelDay dayToDelete : daysToDelete) {
            travelPlaceRepository.deleteAllByTravelDayId(dayToDelete.getTravelDayId());
            travelDayRepository.delete(dayToDelete);
        }

        return CreateMyTravelResponse.from(travel.getTravelId());
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
            List<TravelPlace> travelPlaceList = travelPlaceRepository.findByTravelDayId(travelDay.getTravelDayId());
            if (travelPlaceList == null) {
                throw new TravelPlaceNotFoundException(travel.getTravelId());
            }
            travelPlaces.add(travelPlaceList);
        }

        return GetMyTravelResponse.of(travel, travelDays, travelPlaces);
    }

    /*
     * 내 전체 일정 조회
     * */
    @Transactional(readOnly = true)
    public GetMyTravelsResponse getMyTravels(final int page, final String memberId) {
        Slice<TravelDTO> travels = travelRepository.findByMemberId(memberId, generatePageDesc(page, LARGE_PAGE_SIZE, "travelogueId"));

        return GetMyTravelsResponse.of(
                LARGE_PAGE_SIZE,
                page,
                travels.getNumberOfElements(),
                travels.hasNext(),
                travels.getContent()
        );
    }
}

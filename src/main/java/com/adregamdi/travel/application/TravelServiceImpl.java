package com.adregamdi.travel.application;

import com.adregamdi.place.application.PlaceService;
import com.adregamdi.place.domain.PlaceReview;
import com.adregamdi.place.dto.PlaceReviewDTO;
import com.adregamdi.place.infrastructure.PlaceReviewRepository;
import com.adregamdi.travel.domain.Travel;
import com.adregamdi.travel.domain.TravelDay;
import com.adregamdi.travel.domain.TravelPlace;
import com.adregamdi.travel.dto.TravelDTO;
import com.adregamdi.travel.dto.TravelPlaceDTO;
import com.adregamdi.travel.dto.request.CreateMyTravelRequest;
import com.adregamdi.travel.dto.response.CreateMyTravelResponse;
import com.adregamdi.travel.dto.response.GetMyTravelResponse;
import com.adregamdi.travel.dto.response.GetMyTravelsResponse;
import com.adregamdi.travel.exception.TravelException.InvalidTravelDateException;
import com.adregamdi.travel.exception.TravelException.InvalidTravelStartDateException;
import com.adregamdi.travel.exception.TravelException.TravelNotFoundException;
import com.adregamdi.travel.exception.TravelException.TravelPlaceNotFoundException;
import com.adregamdi.travel.infrastructure.TravelDayRepository;
import com.adregamdi.travel.infrastructure.TravelPlaceRepository;
import com.adregamdi.travel.infrastructure.TravelRepository;
import com.adregamdi.travelogue.infrastructure.TravelogueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.adregamdi.core.constant.Constant.LARGE_PAGE_SIZE;
import static com.adregamdi.core.utils.PageUtil.generatePageDesc;

@Slf4j
@RequiredArgsConstructor
@Service
public class TravelServiceImpl implements TravelService {
    private final PlaceService placeService;
    private final PlaceReviewRepository placeReviewRepository;
    private final TravelRepository travelRepository;
    private final TravelDayRepository travelDayRepository;
    private final TravelPlaceRepository travelPlaceRepository;
    private final TravelogueRepository travelogueRepository;

    /*
     * [일정 등록/수정]
     * */
    @Override
    @Transactional
    public CreateMyTravelResponse createMyTravel(final String currentMemberId, final CreateMyTravelRequest request) {
        validateTravelDates(request);

        Travel travel = createOrUpdateTravel(currentMemberId, request);

        int totalDays = calculateTotalDays(request);
        List<TravelDay> existingDays = travelDayRepository.findAllByTravelId(travel.getTravelId());

        if (request.travelId() == null) {
            handleNewTravel(travel, request, totalDays);
        } else {
            handleExistingTravel(request, totalDays, existingDays);
        }

        return CreateMyTravelResponse.from(travel.getTravelId());
    }

    private void validateTravelDates(final CreateMyTravelRequest request) {
        LocalDate today = LocalDate.now();
        if (request.startDate().isBefore(today)) {
            throw new InvalidTravelStartDateException();
        }
        if (request.startDate().isAfter(request.endDate())) {
            throw new InvalidTravelDateException(request);
        }
    }

    private Travel createOrUpdateTravel(final String currentMemberId, final CreateMyTravelRequest request) {
        if (request.travelId() == null) {
            return travelRepository.save(Travel.builder()
                    .memberId(currentMemberId)
                    .startDate(request.startDate())
                    .endDate(request.endDate())
                    .title(request.title())
                    .build());
        }

        Travel travel = travelRepository.findById(request.travelId())
                .orElseThrow(() -> new TravelNotFoundException(request.travelId()));
        travel.update(currentMemberId, request.startDate(), request.endDate(), request.title());
        return travel;
    }

    private int calculateTotalDays(final CreateMyTravelRequest request) {
        return (int) (ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1);
    }

    private void handleNewTravel(final Travel travel, final CreateMyTravelRequest request, final int totalDays) {
        for (int day = 1; day <= totalDays; day++) {
            LocalDate date = request.startDate().plusDays(day - 1);
            TravelDay travelDay = createOrUpdateTravelDay(travel.getTravelId(), date, day,
                    day <= request.dayList().size() ? request.dayList().get(day - 1).memo() : "");

            if (day <= request.dayList().size() && request.dayList().get(day - 1).placeList() != null) {
                updateTravelPlaces(travelDay.getTravelDayId(), Collections.emptyList(), request.dayList().get(day - 1).placeList());
            }
        }
    }

    private void handleExistingTravel(final CreateMyTravelRequest request, final int totalDays, final List<TravelDay> existingDays) {
        if (request.dayList() == null || request.dayList().isEmpty()) {
            clearAllTravelDays(existingDays, request.startDate());
            return;
        }

        for (int i = 0; i < Math.min(request.dayList().size(), totalDays); i++) {
            TravelDay existingDay = existingDays.get(i);
            CreateMyTravelRequest.DayInfo dayInfo = request.dayList().get(i);

            updateTravelDay(existingDay, request.startDate().plusDays(i), i + 1, dayInfo.memo());

            List<TravelPlace> existingPlaces = travelPlaceRepository.findAllByTravelDayId(existingDay.getTravelDayId());
            updateTravelPlaces(existingDay.getTravelDayId(), existingPlaces, dayInfo.placeList());
        }

        clearRemainingDays(existingDays, request.dayList().size(), totalDays, request.startDate());
    }

    private TravelDay createOrUpdateTravelDay(
            final Long travelId,
            final LocalDate date,
            final int day,
            final String memo
    ) {
        TravelDay travelDay = new TravelDay(travelId, date, day, memo);
        return travelDayRepository.save(travelDay);
    }

    private void updateTravelDay(
            final TravelDay travelDay,
            final LocalDate date,
            final int day,
            final String memo
    ) {
        travelDay.update(date, day, memo);
        travelDayRepository.save(travelDay);
    }

    private void updateTravelPlaces(
            final Long travelDayId,
            final List<TravelPlace> existingPlaces,
            final List<CreateMyTravelRequest.PlaceInfo> newPlaces
    ) {
        if (newPlaces == null || newPlaces.isEmpty()) {
            deleteTravelPlaces(existingPlaces);
            return;
        }

        for (int i = 0; i < Math.max(existingPlaces.size(), newPlaces.size()); i++) {
            if (i < existingPlaces.size() && i < newPlaces.size()) {
                updateTravelPlace(existingPlaces.get(i), newPlaces.get(i));
            } else if (i < newPlaces.size()) {
                createTravelPlace(travelDayId, newPlaces.get(i));
            } else {
                deleteTravelPlace(existingPlaces.get(i));
            }
        }
    }

    private void updateTravelPlace(final TravelPlace existingPlace, final CreateMyTravelRequest.PlaceInfo newPlace) {
        existingPlace.update(newPlace.placeId(), newPlace.placeOrder());
        travelPlaceRepository.save(existingPlace);
        placeService.addCount(newPlace.placeId(), true);
    }

    private void createTravelPlace(final Long travelDayId, final CreateMyTravelRequest.PlaceInfo placeInfo) {
        TravelPlace newPlace = new TravelPlace(travelDayId, placeInfo.placeId(), placeInfo.placeOrder());
        travelPlaceRepository.save(newPlace);
        placeService.addCount(placeInfo.placeId(), true);
    }

    private void deleteTravelPlaces(final List<TravelPlace> places) {
        for (TravelPlace place : places) {
            deleteTravelPlace(place);
        }
    }

    private void deleteTravelPlace(final TravelPlace place) {
        travelPlaceRepository.delete(place);
        placeService.addCount(place.getPlaceId(), false);
    }

    private void clearAllTravelDays(final List<TravelDay> days, final LocalDate startDate) {
        for (int i = 0; i < days.size(); i++) {
            TravelDay day = days.get(i);
            updateTravelDay(day, startDate.plusDays(i), i + 1, "");
            deleteTravelPlaces(travelPlaceRepository.findAllByTravelDayId(day.getTravelDayId()));
        }
    }

    private void clearRemainingDays(
            final List<TravelDay> existingDays,
            final int filledDays,
            final int totalDays,
            final LocalDate startDate
    ) {
        for (int i = filledDays; i < totalDays; i++) {
            TravelDay day = existingDays.get(i);
            updateTravelDay(day, startDate.plusDays(i), i + 1, "");
            deleteTravelPlaces(travelPlaceRepository.findAllByTravelDayId(day.getTravelDayId()));
        }
    }

    /*
     * [내 특정 일정 조회]
     * */
    @Override
    @Transactional(readOnly = true)
    public GetMyTravelResponse getMyTravel(final String currentMemberId, final Long travelId) {
        Travel travel = findTravelByIdAndMemberId(travelId, currentMemberId);
        List<TravelDay> travelDays = travelDayRepository.findByTravelId(travelId);
        List<List<TravelPlaceDTO>> travelPlaceDTOList = getTravelPlaceDTOList(currentMemberId, travelDays);
        boolean hasTravelogue = travelogueRepository.findByTravelId(travelId) != null;

        return GetMyTravelResponse.of(hasTravelogue, travel, travelDays, travelPlaceDTOList);
    }

    private Travel findTravelByIdAndMemberId(final Long travelId, final String memberId) {
        return travelRepository.findByTravelIdAndMemberId(travelId, memberId)
                .orElseThrow(() -> new TravelNotFoundException(travelId));
    }

    private List<List<TravelPlaceDTO>> getTravelPlaceDTOList(final String memberId, final List<TravelDay> travelDays) {
        return travelDays.stream()
                .map(day -> getTravelPlaceDTOs(memberId, day))
                .collect(Collectors.toList());
    }

    private List<TravelPlaceDTO> getTravelPlaceDTOs(final String memberId, final TravelDay travelDay) {
        List<TravelPlace> travelPlaces = travelPlaceRepository.findByTravelDayId(travelDay.getTravelDayId());
        if (travelPlaces == null) {
            throw new TravelPlaceNotFoundException(travelDay.getTravelId());
        }
        return travelPlaces.stream()
                .map(place -> createTravelPlaceDTO(memberId, place))
                .collect(Collectors.toList());
    }

    private TravelPlaceDTO createTravelPlaceDTO(final String memberId, final TravelPlace travelPlace) {
        PlaceReview placeReviewInfo = placeReviewRepository.findByMemberIdAndPlaceId(memberId, travelPlace.getPlaceId())
                .orElse(PlaceReview.builder().build());
        PlaceReviewDTO placeReview = null;
        if (placeReviewInfo.getPlaceReviewId() != null && placeReviewInfo.getPlaceReviewId() != 0) {
            placeReview = placeService.getReview(memberId, placeReviewInfo.getPlaceReviewId());
        }
        return TravelPlaceDTO.of(placeReview, travelPlace, placeService.get(memberId, travelPlace.getPlaceId()).place());
    }

    /*
     * [내 전체 일정 조회]
     * */
    @Override
    @Transactional(readOnly = true)
    public GetMyTravelsResponse getMyTravels(final String currentMemberId, final int page) {
        Slice<TravelDTO> travels = travelRepository.findByMemberId(currentMemberId, generatePageDesc(page, LARGE_PAGE_SIZE, "travelId"));

        return GetMyTravelsResponse.of(
                LARGE_PAGE_SIZE,
                page,
                travels.getNumberOfElements(),
                travels.hasNext(),
                travels.getContent()
        );
    }

    /*
     * [내 특정 일정 삭제]
     * */
    @Override
    @Transactional
    public void deleteMyTravel(final String currentMemberId, final Long travelId) {
        Travel travel = findTravelByIdAndMemberId(travelId, currentMemberId);

        List<TravelDay> travelDays = travelDayRepository.findAllByTravelId(travel.getTravelId());
        if (!travelDays.isEmpty()) {
            deleteTravelDaysAndPlaces(travelDays);
        }

        travelRepository.delete(travel);
    }

    private void deleteTravelDaysAndPlaces(final List<TravelDay> travelDays) {
        for (TravelDay travelDay : travelDays) {
            deleteTravelPlacesForDay(travelDay);
        }
        travelDayRepository.deleteAll(travelDays);
    }

    private void deleteTravelPlacesForDay(final TravelDay travelDay) {
        List<TravelPlace> travelPlaces = travelPlaceRepository.findAllByTravelDayId(travelDay.getTravelDayId());
        travelPlaceRepository.deleteAllByTravelDayId(travelDay.getTravelDayId());
        decrementPlaceCounts(travelPlaces);
    }

    private void decrementPlaceCounts(final List<TravelPlace> travelPlaces) {
        for (TravelPlace travelPlace : travelPlaces) {
            placeService.addCount(travelPlace.getPlaceId(), false);
        }
    }
}

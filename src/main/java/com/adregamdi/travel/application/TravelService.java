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
import com.adregamdi.travel.exception.TravelException.TravelDayNotFoundException;
import com.adregamdi.travel.exception.TravelException.TravelNotFoundException;
import com.adregamdi.travel.exception.TravelException.TravelPlaceNotFoundException;
import com.adregamdi.travel.infrastructure.TravelDayRepository;
import com.adregamdi.travel.infrastructure.TravelPlaceRepository;
import com.adregamdi.travel.infrastructure.TravelRepository;
import com.adregamdi.travelogue.domain.Travelogue;
import com.adregamdi.travelogue.infrastructure.TravelogueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.adregamdi.core.constant.Constant.LARGE_PAGE_SIZE;
import static com.adregamdi.core.utils.PageUtil.generatePageDesc;

@Slf4j
@RequiredArgsConstructor
@Service
public class TravelService {
    private final PlaceService placeService;
    private final PlaceReviewRepository placeReviewRepository;
    private final TravelRepository travelRepository;
    private final TravelDayRepository travelDayRepository;
    private final TravelPlaceRepository travelPlaceRepository;
    private final TravelogueRepository travelogueRepository;

    /*
     * 일정 등록/수정
     * */
    @Transactional
    public CreateMyTravelResponse createMyTravel(final String currentMemberId, final CreateMyTravelRequest request) {
        int totalDays = (int) (ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1);

        if (request.startDate().isAfter(request.endDate())) {
            throw new InvalidTravelDateException(request);
        }

        Travel travel;
        if (request.travelId() == null) {
            travel = travelRepository.save(Travel.builder()
                    .memberId(currentMemberId)
                    .startDate(request.startDate())
                    .endDate(request.endDate())
                    .title(request.title())
                    .build());
        } else {
            travel = travelRepository.findById(request.travelId())
                    .orElseThrow(() -> new TravelNotFoundException(request.travelId()));
            travel.update(currentMemberId, request.startDate(), request.endDate(), request.title());
        }

        List<TravelDay> existingDays = travelDayRepository.findAllByTravelId(travel.getTravelId());
        // 첫 등록 시
        if (request.travelId() == null) {
            for (int day = 1; day <= totalDays; day++) {
                LocalDate date = request.startDate().plusDays(day - 1);
                TravelDay travelDay;
                if (request.dayList().size() > day - 1) {
                    travelDay = new TravelDay(travel.getTravelId(), date, day, request.dayList().get(day - 1).memo());
                    travelDayRepository.save(travelDay);
                    if (request.dayList().get(day - 1).placeList() != null && !request.dayList().get(day - 1).placeList().isEmpty()) {
                        for (int i = 0; i < request.dayList().get(day - 1).placeList().size(); i++) {
                            travelPlaceRepository.save(new TravelPlace(travelDay.getTravelDayId(), request.dayList().get(day - 1).placeList().get(i).placeId(), request.dayList().get(day - 1).placeList().get(i).placeOrder()));
                            placeService.addCount(request.dayList().get(day - 1).placeList().get(i).placeId(), true);
                        }
                    }
                } else {
                    travelDay = new TravelDay(travel.getTravelId(), date, day, "");
                    travelDayRepository.save(travelDay);
                }
            }
            return CreateMyTravelResponse.from(travel.getTravelId());
        }

        // 기존 데이터 수정 시
        // 하루도 설정하지 않으면
        if (request.dayList() == null || request.dayList().isEmpty()) {
            for (int day = 1; day <= totalDays; day++) {
                LocalDate date = request.startDate().plusDays(day - 1);
                List<TravelPlace> travelPlaces = travelPlaceRepository.findAllByTravelDayId(existingDays.get(day - 1).getTravelDayId());

                if (!travelPlaces.isEmpty()) {
                    for (TravelPlace travelPlace : travelPlaces) {
                        placeService.addCount(travelPlace.getPlaceId(), false);
                    }
                }
                existingDays.get(day - 1).update(date, day, "");
                travelPlaceRepository.deleteAllByTravelDayId(existingDays.get(day - 1).getTravelDayId());
            }
            return CreateMyTravelResponse.from(travel.getTravelId());
        }
        // 하루 이상 설정하면
        // 전체 기간에서 요청된 날짜만큼 채우기
        for (int i = 0; i < request.dayList().size(); i++) {
            existingDays.get(i).update(request.dayList().get(i).date(), request.dayList().get(i).day(), request.dayList().get(i).memo());

            List<TravelPlace> travelPlaces = travelPlaceRepository.findAllByTravelDayId(existingDays.get(i).getTravelDayId());

            if (request.dayList().get(i).placeList() == null || request.dayList().get(i).placeList().isEmpty() && !travelPlaces.isEmpty()) {
                travelPlaceRepository.deleteAllByTravelDayId(existingDays.get(i).getTravelDayId());
                for (TravelPlace travelPlace : travelPlaces) {
                    placeService.addCount(travelPlace.getPlaceId(), false);
                }
            } else if (request.dayList().get(i).placeList() != null && !request.dayList().get(i).placeList().isEmpty() && travelPlaces.isEmpty()) {
                travelPlaces = new ArrayList<>();
                for (int j = 0; j < request.dayList().get(i).placeList().size(); j++) {
                    travelPlaces.add(new TravelPlace(existingDays.get(i).getTravelDayId(), request.dayList().get(i).placeList().get(j).placeId(), request.dayList().get(i).placeList().get(j).placeOrder()));
                    placeService.addCount(request.dayList().get(i).placeList().get(j).placeId(), true);
                }
                travelPlaceRepository.saveAll(travelPlaces);
            } else if (request.dayList().get(i).placeList() != null && !request.dayList().get(i).placeList().isEmpty() && !travelPlaces.isEmpty()) {
                if (travelPlaces.size() > request.dayList().get(i).placeList().size()) {
                    for (int j = 0; j < travelPlaces.size(); j++) {
                        if (request.dayList().get(i).placeList().size() > j) {
                            travelPlaces.get(j).update(request.dayList().get(i).placeList().get(j).placeId(), request.dayList().get(i).placeList().get(j).placeOrder());
                            placeService.addCount(request.dayList().get(i).placeList().get(j).placeId(), true);
                        } else {
                            travelPlaceRepository.deleteByTravelDayIdAndPlaceId(existingDays.get(i).getTravelDayId(), travelPlaces.get(j).getPlaceId());
                            placeService.addCount(travelPlaces.get(j).getPlaceId(), false);
                        }
                    }
                } else if (travelPlaces.size() == request.dayList().get(i).placeList().size()) {
                    for (int j = 0; j < travelPlaces.size(); j++) {
                        travelPlaces.get(j).update(request.dayList().get(i).placeList().get(j).placeId(), request.dayList().get(i).placeList().get(j).placeOrder());
                        placeService.addCount(request.dayList().get(i).placeList().get(j).placeId(), true);
                    }
                } else {
                    for (int j = 0; j < request.dayList().get(i).placeList().size(); j++) {
                        if (travelPlaces.size() > j) {
                            travelPlaces.get(j).update(request.dayList().get(i).placeList().get(j).placeId(), request.dayList().get(i).placeList().get(j).placeOrder());
                            placeService.addCount(request.dayList().get(i).placeList().get(j).placeId(), true);
                        } else {
                            travelPlaces.add(new TravelPlace(existingDays.get(i).getTravelDayId(), request.dayList().get(i).placeList().get(j).placeId(), request.dayList().get(i).placeList().get(j).placeOrder()));
                            placeService.addCount(request.dayList().get(i).placeList().get(j).placeId(), true);
                        }
                        travelPlaceRepository.saveAll(travelPlaces);
                    }
                }
            }
        }
        // 전체 기간에서 비어있는 날짜 지우기
        for (int day = request.dayList().size() + 1; day <= totalDays; day++) {
            LocalDate date = request.startDate().plusDays(day - 1);
            List<TravelPlace> travelPlaces = travelPlaceRepository.findAllByTravelDayId(existingDays.get(day - 1).getTravelDayId());

            if (!travelPlaces.isEmpty()) {
                for (TravelPlace travelPlace : travelPlaces) {
                    placeService.addCount(travelPlace.getPlaceId(), false);
                }
            }
            existingDays.get(day - 1).update(date, day, "");
            travelPlaceRepository.deleteAllByTravelDayId(existingDays.get(day - 1).getTravelDayId());
        }

        return CreateMyTravelResponse.from(travel.getTravelId());
    }

    /*
     * 내 특정 일정 조회
     * */
    @Transactional(readOnly = true)
    public GetMyTravelResponse getMyTravel(final String currentMemberId, final Long travelId) {
        Travel travel = travelRepository.findByTravelIdAndMemberId(travelId, currentMemberId)
                .orElseThrow(() -> new TravelNotFoundException(currentMemberId));

        List<TravelDay> travelDays = travelDayRepository.findByTravelId(travelId)
                .orElseThrow(() -> new TravelDayNotFoundException(travelId));

        List<List<TravelPlaceDTO>> travelPlaceDTOList = new ArrayList<>();
        for (TravelDay travelDay : travelDays) {
            List<TravelPlace> travelPlaceList = travelPlaceRepository.findByTravelDayId(travelDay.getTravelDayId());
            List<TravelPlaceDTO> travelPlaceDTOS = new ArrayList<>();
            if (travelPlaceList == null) {
                throw new TravelPlaceNotFoundException(travel.getTravelId());
            }
            for (TravelPlace travelPlace : travelPlaceList) {
                PlaceReview placeReviewInfo = placeReviewRepository.findByMemberIdAndPlaceId(currentMemberId, travelPlace.getPlaceId())
                        .orElse(PlaceReview.builder().build());
                PlaceReviewDTO placeReview = null;
                if (placeReviewInfo.getPlaceReviewId() != null && placeReviewInfo.getPlaceReviewId() != 0) {
                    placeReview = placeService.getReview(currentMemberId, placeReviewInfo.getPlaceReviewId());
                }
                travelPlaceDTOS.add(TravelPlaceDTO.of(placeReview, travelPlace, placeService.get(currentMemberId, travelPlace.getPlaceId()).place()));
            }
            travelPlaceDTOList.add(travelPlaceDTOS);
        }

        Travelogue travelogue = travelogueRepository.findByTravelId(travelId);
        return GetMyTravelResponse.of(travelogue != null, travel, travelDays, travelPlaceDTOList);
    }

    /*
     * 내 전체 일정 조회
     * */
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
     * 내 특정 일정 삭제
     * */
    @Transactional
    public void deleteMyTravel(final String currentMemberId, final Long travelId) {
        Travel travel = travelRepository.findByTravelIdAndMemberId(travelId, currentMemberId)
                .orElseThrow(() -> new TravelNotFoundException(travelId));

        List<TravelDay> travelDays = travelDayRepository.findAllByTravelId(travel.getTravelId());
        for (TravelDay travelday : travelDays) {
            List<TravelPlace> travelPlaces = travelPlaceRepository.findAllByTravelDayId(travelday.getTravelDayId());

            travelPlaceRepository.deleteAll(travelPlaces);
            
            for (TravelPlace travelPlace : travelPlaces) {
                placeService.addCount(travelPlace.getPlaceId(), false);
            }
        }

        travelDayRepository.deleteAll(travelDays);
        travelRepository.delete(travel);
    }
}

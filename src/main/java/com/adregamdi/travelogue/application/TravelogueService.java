package com.adregamdi.travelogue.application;

import com.adregamdi.place.domain.PlaceReview;
import com.adregamdi.place.domain.PlaceReviewImage;
import com.adregamdi.place.exception.PlaceException.PlaceReviewImageNotFoundException;
import com.adregamdi.place.exception.PlaceException.PlaceReviewNotFoundException;
import com.adregamdi.place.infrastructure.PlaceReviewImageRepository;
import com.adregamdi.place.infrastructure.PlaceReviewRepository;
import com.adregamdi.travelogue.domain.Travelogue;
import com.adregamdi.travelogue.domain.TravelogueDay;
import com.adregamdi.travelogue.domain.TravelogueImage;
import com.adregamdi.travelogue.dto.request.CreateMyTravelogueRequest;
import com.adregamdi.travelogue.dto.response.GetTravelogueResponse;
import com.adregamdi.travelogue.exception.TravelogueException.TravelogueDayNotFoundException;
import com.adregamdi.travelogue.exception.TravelogueException.TravelogueImageNotFoundException;
import com.adregamdi.travelogue.exception.TravelogueException.TravelogueNotFoundException;
import com.adregamdi.travelogue.infrastructure.TravelogueDayRepository;
import com.adregamdi.travelogue.infrastructure.TravelogueImageRepository;
import com.adregamdi.travelogue.infrastructure.TravelogueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TravelogueService {
    private final TravelogueRepository travelogueRepository;
    private final TravelogueImageRepository travelogueImageRepository;
    private final TravelogueDayRepository travelogueDayRepository;
    private final PlaceReviewRepository placeReviewRepository;
    private final PlaceReviewImageRepository placeReviewImageRepository;

    /*
     * 여행기 등록
     */
    @Transactional
    public void createMyTravelogue(final CreateMyTravelogueRequest request, final String memberId) {
        Travelogue travelogue = travelogueRepository.save(new Travelogue(memberId, request.travelId(), request.title(), request.introduction()));
        saveTravelogueImages(request.travelogueImageList(), travelogue.getTravelogueId());
        saveTravelogueDaysAndReviews(request.dayList(), travelogue.getTravelogueId(), memberId);
    }

    /*
     * 여행기 조회
     */
    @Transactional(readOnly = true)
    public GetTravelogueResponse get(final Long travelogueId) {
        Travelogue travelogue = travelogueRepository.findById(travelogueId)
                .orElseThrow(() -> new TravelogueNotFoundException(travelogueId));

        List<TravelogueImage> travelogueImages = travelogueImageRepository.findByTravelogueId(travelogueId)
                .orElseThrow(() -> new TravelogueImageNotFoundException(travelogueId));

        List<TravelogueDay> travelogueDays = travelogueDayRepository.findByTravelogueIdOrderByDay(travelogueId)
                .orElseThrow(() -> new TravelogueDayNotFoundException(travelogueId));

        List<PlaceReview> placeReviews = placeReviewRepository.findByTravelogueId(travelogueId)
                .orElseThrow(() -> new PlaceReviewNotFoundException(travelogueId));

        Function<Long, List<PlaceReviewImage>> placeReviewImagesFetcher = (reviewId) ->
                placeReviewImageRepository.findByPlaceReviewId(reviewId)
                        .orElseThrow(() -> new PlaceReviewImageNotFoundException(reviewId));

        return GetTravelogueResponse.of(travelogue, travelogueImages, travelogueDays, placeReviews, placeReviewImagesFetcher);
    }

    private void saveTravelogueImages(final List<CreateMyTravelogueRequest.TravelogueImageInfo> images, final Long travelogueId) {
        List<CreateMyTravelogueRequest.TravelogueImageInfo> imageList = (images != null) ? images : Collections.emptyList();

        List<TravelogueImage> travelogueImages = imageList.stream()
                .map(img -> new TravelogueImage(travelogueId, img.url()))
                .collect(Collectors.toList());

        travelogueImageRepository.saveAll(travelogueImages);
    }

    private void saveTravelogueDaysAndReviews(final List<CreateMyTravelogueRequest.DayInfo> dayList, final Long travelogueId, final String memberId) {
        List<CreateMyTravelogueRequest.DayInfo> days = (dayList != null) ? dayList : Collections.emptyList();

        for (CreateMyTravelogueRequest.DayInfo dayInfo : days) {
            travelogueDayRepository.save(new TravelogueDay(travelogueId, dayInfo.date(), dayInfo.day(), dayInfo.content()));
            savePlaceReviews(dayInfo.placeReviewList(), travelogueId, memberId);
        }
    }

    private void savePlaceReviews(final List<CreateMyTravelogueRequest.PlaceReviewInfo> placeReviews, final Long travelogueId, final String memberId) {
        List<CreateMyTravelogueRequest.PlaceReviewInfo> reviews = (placeReviews != null) ? placeReviews : Collections.emptyList();

        for (CreateMyTravelogueRequest.PlaceReviewInfo reviewInfo : reviews) {
            PlaceReview placeReview = placeReviewRepository.save(new PlaceReview(memberId, reviewInfo.placeId(), travelogueId, reviewInfo.content()));
            savePlaceReviewImages(reviewInfo.placeReviewImageList(), placeReview.getPlaceReviewId());
        }
    }

    private void savePlaceReviewImages(final List<CreateMyTravelogueRequest.PlaceReviewImageInfo> images, final Long placeReviewId) {
        List<CreateMyTravelogueRequest.PlaceReviewImageInfo> imageList = (images != null) ? images : Collections.emptyList();

        List<PlaceReviewImage> placeReviewImages = imageList.stream()
                .map(img -> new PlaceReviewImage(placeReviewId, img.url()))
                .collect(Collectors.toList());

        placeReviewImageRepository.saveAll(placeReviewImages);
    }
}

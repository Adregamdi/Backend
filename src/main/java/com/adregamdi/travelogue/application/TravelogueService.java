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
import com.adregamdi.travelogue.dto.TravelogueDTO;
import com.adregamdi.travelogue.dto.request.CreateMyTravelogueRequest;
import com.adregamdi.travelogue.dto.response.CreateMyTravelogueResponse;
import com.adregamdi.travelogue.dto.response.GetMyTraveloguesResponse;
import com.adregamdi.travelogue.dto.response.GetTravelogueResponse;
import com.adregamdi.travelogue.exception.TravelogueException.TravelogueDayNotFoundException;
import com.adregamdi.travelogue.exception.TravelogueException.TravelogueImageNotFoundException;
import com.adregamdi.travelogue.exception.TravelogueException.TravelogueNotFoundException;
import com.adregamdi.travelogue.infrastructure.TravelogueDayRepository;
import com.adregamdi.travelogue.infrastructure.TravelogueImageRepository;
import com.adregamdi.travelogue.infrastructure.TravelogueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.adregamdi.core.constant.Constant.LARGE_PAGE_SIZE;
import static com.adregamdi.core.utils.PageUtil.generatePageDesc;

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
    public CreateMyTravelogueResponse createMyTravelogue(
            final CreateMyTravelogueRequest request,
            final String memberId
    ) {
        Travelogue travelogue;
        List<CreateMyTravelogueRequest.TravelogueImageInfo> travelogueImageUrls = new ArrayList<>();
        if (request.travelogueImageList() == null || request.travelogueImageList().isEmpty()) {
            travelogueImageUrls.add(new CreateMyTravelogueRequest.TravelogueImageInfo("https://adregamdi-dev2.s3.ap-northeast-2.amazonaws.com/profile/default_profile_image.png"));
        } else {
            travelogueImageUrls = request.travelogueImageList();
        }

        if (request.travelogueId() == null) {
            travelogue = travelogueRepository.save(new Travelogue(memberId, request.travelId(), request.title(), request.introduction()));
            saveTravelogueImages(request.travelogueImageList(), travelogue.getTravelogueId());
            saveTravelogueDaysAndReviews(request.dayList(), travelogue.getTravelogueId(), memberId);
        } else {
            travelogue = travelogueRepository.findById(request.travelogueId())
                    .orElseThrow(() -> new TravelogueNotFoundException(request.travelogueId()));
            travelogue.update(request.title(), request.introduction());

            List<TravelogueImage> travelogueImages = travelogueImageRepository.findAllByTravelogueId(request.travelogueId());
            if (!travelogueImages.isEmpty()) {
                // 요청 < 기존
                if (travelogueImages.size() > travelogueImageUrls.size()) {
                    for (int i = 0; i < travelogueImages.size(); i++) {
                        if (travelogueImageUrls.size() > i) {
                            travelogueImages.get(i).update(request.travelogueId(), travelogueImageUrls.get(i).url());
                        } else {
                            travelogueImageRepository.deleteById(travelogueImages.get(i).getTravelogueImageId());
                        }
                    }
                }
                // 요청 == 기존
                else if (travelogueImages.size() == travelogueImageUrls.size()) {
                    for (int i = 0; i < travelogueImages.size(); i++) {
                        travelogueImages.get(i).update(request.travelogueId(), travelogueImageUrls.get(i).url());
                    }
                }
                // 요청 > 기존
                else {
                    for (int i = 0; i < travelogueImageUrls.size(); i++) {
                        List<TravelogueImage> newTravelogueImages = new ArrayList<>();
                        if (travelogueImages.size() > i) {
                            travelogueImages.get(i).update(request.travelogueId(), travelogueImageUrls.get(i).url());
                        } else {
                            newTravelogueImages.add(new TravelogueImage(request.travelogueId(), travelogueImageUrls.get(i).url()));
                        }
                        travelogueImageRepository.saveAll(newTravelogueImages);
                    }
                }
            } else {
                for (CreateMyTravelogueRequest.TravelogueImageInfo travelogueImageUrl : travelogueImageUrls) {
                    travelogueImages.add(new TravelogueImage(request.travelogueId(), travelogueImageUrl.url()));
                }
                travelogueImageRepository.saveAll(travelogueImages);
            }
        }
        return CreateMyTravelogueResponse.from(travelogue.getTravelogueId());
    }

    private void saveTravelogueImages(
            final List<CreateMyTravelogueRequest.TravelogueImageInfo> images,
            final Long travelogueId
    ) {
        List<CreateMyTravelogueRequest.TravelogueImageInfo> imageList = (images != null) ? images : Collections.emptyList();

        List<TravelogueImage> travelogueImages = imageList.stream()
                .map(img -> new TravelogueImage(travelogueId, img.url()))
                .collect(Collectors.toList());

        travelogueImageRepository.saveAll(travelogueImages);
    }

    private void saveTravelogueDaysAndReviews(
            final List<CreateMyTravelogueRequest.DayInfo> dayList,
            final Long travelogueId,
            final String memberId
    ) {
        List<CreateMyTravelogueRequest.DayInfo> days = (dayList != null) ? dayList : Collections.emptyList();

        for (CreateMyTravelogueRequest.DayInfo dayInfo : days) {
            TravelogueDay travelogueDay = travelogueDayRepository.save(new TravelogueDay(travelogueId, dayInfo.date(), dayInfo.day(), dayInfo.content()));
            savePlaceReviews(dayInfo.placeReviewList(), travelogueId, travelogueDay.getTravelogueDayId(), memberId);
        }
    }

    private void savePlaceReviews(
            final List<CreateMyTravelogueRequest.PlaceReviewInfo> placeReviews,
            final Long travelogueId, final Long travelogueDayId,
            final String memberId
    ) {
        List<CreateMyTravelogueRequest.PlaceReviewInfo> reviews = (placeReviews != null) ? placeReviews : Collections.emptyList();

        for (CreateMyTravelogueRequest.PlaceReviewInfo reviewInfo : reviews) {
            PlaceReview placeReview = placeReviewRepository.save(new PlaceReview(memberId, reviewInfo.placeId(), travelogueId, travelogueDayId, reviewInfo.content()));
            savePlaceReviewImages(reviewInfo.placeReviewImageList(), placeReview.getPlaceReviewId());
        }
    }

    private void savePlaceReviewImages(
            final List<CreateMyTravelogueRequest.PlaceReviewImageInfo> images,
            final Long placeReviewId
    ) {
        List<CreateMyTravelogueRequest.PlaceReviewImageInfo> imageList = (images != null) ? images : Collections.emptyList();

        List<PlaceReviewImage> placeReviewImages = imageList.stream()
                .map(img -> new PlaceReviewImage(placeReviewId, img.url()))
                .collect(Collectors.toList());

        placeReviewImageRepository.saveAll(placeReviewImages);
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


    /*
     * 내 전체 여행기 조회
     * */
    @Transactional(readOnly = true)
    public GetMyTraveloguesResponse getMyTravelogues(final int page, final String memberId) {
        Slice<TravelogueDTO> travelogues = travelogueRepository.findByMemberId(memberId, generatePageDesc(page, LARGE_PAGE_SIZE, "travelogueId"));

        return GetMyTraveloguesResponse.of(
                LARGE_PAGE_SIZE,
                page,
                travelogues.getNumberOfElements(),
                travelogues.hasNext(),
                travelogues.getContent()
        );
    }
}

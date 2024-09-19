package com.adregamdi.travelogue.application;

import com.adregamdi.media.application.ImageService;
import com.adregamdi.place.domain.Place;
import com.adregamdi.place.domain.PlaceReview;
import com.adregamdi.place.domain.PlaceReviewImage;
import com.adregamdi.place.exception.PlaceException;
import com.adregamdi.place.infrastructure.PlaceRepository;
import com.adregamdi.place.infrastructure.PlaceReviewImageRepository;
import com.adregamdi.place.infrastructure.PlaceReviewRepository;
import com.adregamdi.travel.domain.Travel;
import com.adregamdi.travel.exception.TravelException;
import com.adregamdi.travel.infrastructure.TravelRepository;
import com.adregamdi.travelogue.domain.Travelogue;
import com.adregamdi.travelogue.domain.TravelogueDay;
import com.adregamdi.travelogue.domain.TravelogueDayPlaceReview;
import com.adregamdi.travelogue.domain.TravelogueImage;
import com.adregamdi.travelogue.dto.TravelogueDTO;
import com.adregamdi.travelogue.dto.request.CreateMyTravelogueRequest;
import com.adregamdi.travelogue.dto.response.CreateMyTravelogueResponse;
import com.adregamdi.travelogue.dto.response.GetMyTraveloguesResponse;
import com.adregamdi.travelogue.dto.response.GetRecentTraveloguesResponse;
import com.adregamdi.travelogue.dto.response.GetTravelogueResponse;
import com.adregamdi.travelogue.exception.TravelogueException;
import com.adregamdi.travelogue.exception.TravelogueException.TravelogueExistException;
import com.adregamdi.travelogue.exception.TravelogueException.TravelogueNotFoundException;
import com.adregamdi.travelogue.infrastructure.TravelogueDayPlaceReviewRepository;
import com.adregamdi.travelogue.infrastructure.TravelogueDayRepository;
import com.adregamdi.travelogue.infrastructure.TravelogueImageRepository;
import com.adregamdi.travelogue.infrastructure.TravelogueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.adregamdi.core.constant.Constant.LARGE_PAGE_SIZE;
import static com.adregamdi.core.utils.PageUtil.generatePageDesc;
import static com.adregamdi.media.domain.ImageTarget.TRAVELOGUE;

@Slf4j
@RequiredArgsConstructor
@Service
public class TravelogueService {
    private final ImageService imageService;
    private final TravelRepository travelRepository;
    private final TravelogueRepository travelogueRepository;
    private final TravelogueImageRepository travelogueImageRepository;
    private final TravelogueDayRepository travelogueDayRepository;
    private final TravelogueDayPlaceReviewRepository travelogueDayPlaceReviewRepository;
    private final PlaceRepository placeRepository;
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
        Travel travel = travelRepository.findById(request.travelId())
                .orElseThrow(() -> new TravelException.TravelNotFoundException(request.travelId()));

        validateTravelEnded(travel);

        Travelogue travelogue;
        List<CreateMyTravelogueRequest.TravelogueImageInfo> requestImages = new ArrayList<>();
        if (request.travelogueImageList() == null || request.travelogueImageList().isEmpty()) {
            requestImages.add(new CreateMyTravelogueRequest.TravelogueImageInfo("https://adregamdi-dev2.s3.ap-northeast-2.amazonaws.com/profile/default_profile_image.png"));
        } else {
            requestImages = request.travelogueImageList();
        }

        // 등록 시
        if (request.travelogueId() == null) {
            travelogue = travelogueRepository.findByTravelId(request.travelId());
            if (travelogue != null) {
                throw new TravelogueExistException(request.travelId());
            }
            travelogue = travelogueRepository.save(new Travelogue(memberId, request.travelId(), request.title(), request.introduction()));
            saveTravelogueImages(requestImages, travelogue.getTravelogueId());
            saveTravelogueDaysAndReviews(request.dayList(), travelogue.getTravelogueId());
        }
        // 수정 시
        else {
            travelogue = travelogueRepository.findById(request.travelogueId())
                    .orElseThrow(() -> new TravelogueNotFoundException(request.travelogueId()));
            travelogue.update(request.title(), request.introduction());

            List<TravelogueImage> travelogueImages = travelogueImageRepository.findAllByTravelogueId(request.travelogueId());
            if (!travelogueImages.isEmpty()) {
                // 요청 < 기존
                if (requestImages.size() < travelogueImages.size()) {
                    for (int i = 0; i < travelogueImages.size(); i++) {
                        if (requestImages.size() > i) {
                            travelogueImages.get(i).update(request.travelogueId(), requestImages.get(i).url());
                        } else {
                            travelogueImageRepository.deleteById(travelogueImages.get(i).getTravelogueImageId());
                        }
                    }
                }
                // 요청 == 기존
                else if (requestImages.size() == travelogueImages.size()) {
                    for (int i = 0; i < travelogueImages.size(); i++) {
                        travelogueImages.get(i).update(request.travelogueId(), requestImages.get(i).url());
                    }
                }
                // 요청 > 기존
                else {
                    for (int i = 0; i < requestImages.size(); i++) {
                        List<TravelogueImage> newTravelogueImages = new ArrayList<>();
                        if (travelogueImages.size() > i) {
                            travelogueImages.get(i).update(request.travelogueId(), requestImages.get(i).url());
                        } else {
                            newTravelogueImages.add(new TravelogueImage(request.travelogueId(), requestImages.get(i).url()));
                        }
                        travelogueImageRepository.saveAll(newTravelogueImages);
                    }
                }
                saveOrUpdateImages(request.travelogueId(), requestImages, false);
            } else {
                for (CreateMyTravelogueRequest.TravelogueImageInfo travelogueImageUrl : requestImages) {
                    travelogueImages.add(new TravelogueImage(request.travelogueId(), travelogueImageUrl.url()));
                }
                travelogueImageRepository.saveAll(travelogueImages);
                saveOrUpdateImages(request.travelogueId(), requestImages, true);
            }
        }
        return CreateMyTravelogueResponse.from(travelogue.getTravelogueId());
    }

    private void validateTravelEnded(final Travel travel) {
        LocalDate today = LocalDate.now();
        if (travel.getEndDate().isAfter(today) || travel.getEndDate().isEqual(today)) {
            throw new TravelogueException.TravelNotEndedException();
        }
    }

    private void saveTravelogueImages(
            final List<CreateMyTravelogueRequest.TravelogueImageInfo> requestImages,
            final Long travelogueId
    ) {
        List<CreateMyTravelogueRequest.TravelogueImageInfo> imageList = (requestImages != null) ? requestImages : Collections.emptyList();

        List<TravelogueImage> travelogueImages = imageList.stream()
                .map(img -> new TravelogueImage(travelogueId, img.url()))
                .collect(Collectors.toList());

        travelogueImageRepository.saveAll(travelogueImages);
        if (requestImages != null) {
            saveOrUpdateImages(travelogueId, requestImages, true);
        }
    }

    private void saveOrUpdateImages(Long travelogueId, List<CreateMyTravelogueRequest.TravelogueImageInfo> travelogueImages, boolean isSave) {
        List<String> urls = travelogueImages.stream()
                .map(CreateMyTravelogueRequest.TravelogueImageInfo::url)
                .toList();
        if (!travelogueImages.isEmpty() && isSave) {
            imageService.saveTargetId(urls, TRAVELOGUE, String.valueOf(travelogueId));
        } else if (!travelogueImages.isEmpty() || !isSave) {
            imageService.updateImages(urls, TRAVELOGUE, String.valueOf(travelogueId));
        }
    }

    private void saveTravelogueDaysAndReviews(
            final List<CreateMyTravelogueRequest.DayInfo> dayList,
            final Long travelogueId
    ) {
        List<CreateMyTravelogueRequest.DayInfo> days = (dayList != null) ? dayList : Collections.emptyList();

        for (CreateMyTravelogueRequest.DayInfo dayInfo : days) {
            TravelogueDay travelogueDay = travelogueDayRepository.save(new TravelogueDay(travelogueId, dayInfo.date(), dayInfo.day(), dayInfo.content()));
            saveTravelogueDayPlaceReview(dayInfo.placeReviewList(), travelogueDay.getTravelogueDayId());
        }
    }

    private void saveTravelogueDayPlaceReview(
            final List<CreateMyTravelogueRequest.PlaceReviewInfo> placeReviews,
            final Long travelogueDayId
    ) {
        List<CreateMyTravelogueRequest.PlaceReviewInfo> reviews = (placeReviews != null) ? placeReviews : Collections.emptyList();

        for (CreateMyTravelogueRequest.PlaceReviewInfo reviewInfo : reviews) {
            travelogueDayPlaceReviewRepository.save(new TravelogueDayPlaceReview(travelogueDayId, reviewInfo.placeReviewId()));
        }
    }

    /*
     * 여행기 조회
     */
    @Transactional(readOnly = true)
    public GetTravelogueResponse get(final Long travelogueId) {
        Travelogue travelogue = travelogueRepository.findById(travelogueId)
                .orElseThrow(() -> new TravelogueNotFoundException(travelogueId));

        List<TravelogueImage> travelogueImages = travelogueImageRepository.findByTravelogueId(travelogueId)
                .orElse(Collections.emptyList());

        List<TravelogueDay> travelogueDays = travelogueDayRepository.findByTravelogueIdOrderByDay(travelogueId)
                .orElse(Collections.emptyList());

        Map<Long, List<GetTravelogueResponse.PlaceReviewInfo>> placeReviewsMap = new LinkedHashMap<>();
        for (TravelogueDay travelogueDay : travelogueDays) {
            List<TravelogueDayPlaceReview> travelogueDayPlaceReviews = travelogueDayPlaceReviewRepository.findByTravelogueDayId(travelogueDay.getTravelogueDayId());

            List<GetTravelogueResponse.PlaceReviewInfo> placeReviewInfos = travelogueDayPlaceReviews.stream()
                    .map(tdpr -> {
                        PlaceReview placeReview = placeReviewRepository.findById(tdpr.getPlaceReviewId())
                                .orElseThrow(() -> new PlaceException.PlaceReviewNotFoundException(tdpr.getPlaceReviewId()));
                        Place place = placeRepository.findById(placeReview.getPlaceId())
                                .orElseThrow(() -> new PlaceException.PlaceNotFoundException(placeReview.getPlaceId()));
                        List<PlaceReviewImage> images = placeReviewImageRepository.findByPlaceReviewIdOrderByPlaceReviewImageIdDesc(placeReview.getPlaceReviewId());

                        return GetTravelogueResponse.PlaceReviewInfo.builder()
                                .placeId(place.getPlaceId())
                                .title(place.getTitle())
                                .contentsLabel(place.getContentsLabel())
                                .regionLabel(place.getRegionLabel())
                                .content(placeReview.getContent())
                                .placeReviewImageList(images.stream()
                                        .map(img -> new GetTravelogueResponse.PlaceReviewImageInfo(img.getUrl()))
                                        .collect(Collectors.toList()))
                                .build();
                    })
                    .collect(Collectors.toList());

            placeReviewsMap.put(travelogueDay.getTravelogueDayId(), placeReviewInfos);
        }

        return GetTravelogueResponse.of(travelogue, travelogueImages, travelogueDays, placeReviewsMap);
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

    public GetRecentTraveloguesResponse getRecentTravelogues(final int page) {
        Slice<TravelogueDTO> travelogues = travelogueRepository.findOrderByCreatedAt(generatePageDesc(page, LARGE_PAGE_SIZE, "createdAt"));

        return GetRecentTraveloguesResponse.of(
                LARGE_PAGE_SIZE,
                page,
                travelogues.getNumberOfElements(),
                travelogues.hasNext(),
                travelogues.getContent()
        );
    }
}

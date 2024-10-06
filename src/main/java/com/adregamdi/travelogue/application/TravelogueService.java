package com.adregamdi.travelogue.application;

import com.adregamdi.block.exception.BlockException;
import com.adregamdi.block.infrastructure.BlockRepository;
import com.adregamdi.like.application.LikesService;
import com.adregamdi.like.domain.enumtype.ContentType;
import com.adregamdi.media.application.ImageService;
import com.adregamdi.member.domain.Member;
import com.adregamdi.member.exception.MemberException;
import com.adregamdi.member.infrastructure.MemberRepository;
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
import com.adregamdi.travelogue.dto.response.*;
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
    private final LikesService likesService;
    private final TravelRepository travelRepository;
    private final TravelogueRepository travelogueRepository;
    private final TravelogueImageRepository travelogueImageRepository;
    private final TravelogueDayRepository travelogueDayRepository;
    private final TravelogueDayPlaceReviewRepository travelogueDayPlaceReviewRepository;
    private final PlaceRepository placeRepository;
    private final PlaceReviewRepository placeReviewRepository;
    private final PlaceReviewImageRepository placeReviewImageRepository;
    private final MemberRepository memberRepository;
    private final BlockRepository blockRepository;

    /*
     * 여행기 등록
     */
    @Transactional
    public CreateMyTravelogueResponse createMyTravelogue(
            final String currentMemberId,
            final CreateMyTravelogueRequest request
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
            travelogue = travelogueRepository.save(new Travelogue(currentMemberId, request.travelId(), request.title(), request.introduction()));
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
                saveOrUpdateImages(request.travelogueId(), requestImages);
            } else {
                for (CreateMyTravelogueRequest.TravelogueImageInfo travelogueImageUrl : requestImages) {
                    travelogueImages.add(new TravelogueImage(request.travelogueId(), travelogueImageUrl.url()));
                }
                travelogueImageRepository.saveAll(travelogueImages);
                saveOrUpdateImages(request.travelogueId(), requestImages);
            }

            List<TravelogueDay> travelogueDays = travelogueDayRepository.findByTravelogueIdOrderByDay(travelogue.getTravelogueId())
                    .orElse(Collections.emptyList());
            List<CreateMyTravelogueRequest.DayInfo> days = (request.dayList() != null) ? request.dayList() : Collections.emptyList();
            for (int i = 0; i < travelogueDays.size(); i++) {
                List<TravelogueDayPlaceReview> travelogueDayPlaceReviews = travelogueDayPlaceReviewRepository.findByTravelogueDayId(request.travelogueId());

                for (int j = 0; j < travelogueDayPlaceReviews.size(); j++) {
                    if (travelogueDayPlaceReviews.get(j).getPlaceReviewId() == null) {
                        travelogueDayPlaceReviews.get(j).update(
                                travelogueDayPlaceReviews.get(j).getTravelogueDayId(),
                                travelogueDayPlaceReviews.get(j).getPlaceId(),
                                days.get(i).placeList().get(j).placeReviewId());
                    }
                }
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
            saveOrUpdateImages(travelogueId, requestImages);
        }
    }

    private void saveOrUpdateImages(Long travelogueId, List<CreateMyTravelogueRequest.TravelogueImageInfo> travelogueImages) {
        List<String> urls = travelogueImages.stream()
                .map(CreateMyTravelogueRequest.TravelogueImageInfo::url)
                .toList();

        imageService.saveTargetId(urls, TRAVELOGUE, String.valueOf(travelogueId));
    }

    private void saveTravelogueDaysAndReviews(
            final List<CreateMyTravelogueRequest.DayInfo> dayList,
            final Long travelogueId
    ) {
        List<CreateMyTravelogueRequest.DayInfo> days = (dayList != null) ? dayList : Collections.emptyList();

        for (CreateMyTravelogueRequest.DayInfo dayInfo : days) {
            TravelogueDay travelogueDay = travelogueDayRepository.save(TravelogueDay.builder()
                    .travelogueId(travelogueId)
                    .date(dayInfo.date())
                    .day(dayInfo.day())
                    .content(dayInfo.content())
                    .build());
            saveTravelogueDayPlaceReview(dayInfo.placeList(), travelogueDay.getTravelogueDayId());
        }
    }

    private void saveTravelogueDayPlaceReview(
            final List<CreateMyTravelogueRequest.PlaceInfo> places,
            final Long travelogueDayId
    ) {
        List<CreateMyTravelogueRequest.PlaceInfo> placeInfos = (places != null) ? places : Collections.emptyList();

        for (CreateMyTravelogueRequest.PlaceInfo placeInfo : placeInfos) {
            travelogueDayPlaceReviewRepository.save(TravelogueDayPlaceReview.builder()
                    .travelogueDayId(travelogueDayId)
                    .placeId(placeInfo.placeId())
                    .placeReviewId(placeInfo.placeReviewId())
                    .build());
        }
    }

    /*
     * 특정 여행기 조회
     */
    @Transactional(readOnly = true)
    public GetTravelogueResponse get(final String currentMemberId, final Long travelogueId) {
        Travelogue travelogue = travelogueRepository.findById(travelogueId)
                .orElseThrow(() -> new TravelogueNotFoundException(travelogueId));

        Member member = memberRepository.findById(travelogue.getMemberId())
                .orElseThrow(() -> new MemberException.MemberNotFoundException(travelogue.getMemberId()));

        blockRepository.findByBlockedMemberIdAndBlockingMemberId(travelogue.getMemberId(), currentMemberId)
                .ifPresent(BlockException.BlockExistException::new);

        List<TravelogueImage> travelogueImages = travelogueImageRepository.findByTravelogueId(travelogueId)
                .orElse(Collections.emptyList());

        List<TravelogueDay> travelogueDays = travelogueDayRepository.findByTravelogueIdOrderByDay(travelogueId)
                .orElse(Collections.emptyList());

        Map<Long, List<GetTravelogueResponse.PlaceInfo>> placeReviewsMap = new LinkedHashMap<>();
        for (TravelogueDay travelogueDay : travelogueDays) {
            List<TravelogueDayPlaceReview> travelogueDayPlaceReviews = travelogueDayPlaceReviewRepository.findByTravelogueDayId(travelogueDay.getTravelogueDayId());

            List<GetTravelogueResponse.PlaceInfo> placeReviewInfos = travelogueDayPlaceReviews.stream()
                    .map(tdpr -> {
                        Place place = placeRepository.findById(tdpr.getPlaceId())
                                .orElseThrow(() -> new PlaceException.PlaceNotFoundException(tdpr.getPlaceId()));

                        PlaceReview placeReview = PlaceReview.builder().build();
                        if (tdpr.getPlaceReviewId() != null) {
                            placeReview = placeReviewRepository.findById(tdpr.getPlaceReviewId())
                                    .orElseThrow(() -> new PlaceException.PlaceReviewNotFoundException(tdpr.getPlaceReviewId()));
                        }

                        List<PlaceReviewImage> images = placeReviewImageRepository.findByPlaceReviewIdOrderByPlaceReviewImageIdDesc(placeReview.getPlaceReviewId());

                        return GetTravelogueResponse.PlaceInfo.builder()
                                .placeId(place.getPlaceId())
                                .placeReviewId(placeReview.getPlaceReviewId())
                                .title(place.getTitle())
                                .contentsLabel(place.getContentsLabel())
                                .regionLabel(place.getRegionLabel())
                                .latitude(place.getLatitude())
                                .longitude(place.getLongitude())
                                .content(placeReview.getContent())
                                .placeReviewImageList(images.stream()
                                        .map(img -> new GetTravelogueResponse.PlaceReviewImageInfo(img.getUrl()))
                                        .collect(Collectors.toList()))
                                .build();
                    })
                    .collect(Collectors.toList());

            placeReviewsMap.put(travelogueDay.getTravelogueDayId(), placeReviewInfos);
        }
        boolean isLiked = likesService.checkIsLiked(currentMemberId, ContentType.TRAVELOGUE, travelogueId);
        return GetTravelogueResponse.of(isLiked, member, travelogue, travelogueImages, travelogueDays, placeReviewsMap);
    }

    /*
     * 내 전체 여행기 조회
     * */
    @Transactional(readOnly = true)
    public GetMyTraveloguesResponse getMyTravelogues(final String currentMemberId, final int page) {
        Slice<TravelogueDTO> travelogues = travelogueRepository.findByMemberId(currentMemberId, generatePageDesc(page, LARGE_PAGE_SIZE, "travelogueId"));

        return GetMyTraveloguesResponse.of(
                LARGE_PAGE_SIZE,
                page,
                travelogues.getNumberOfElements(),
                travelogues.hasNext(),
                travelogues.getContent()
        );
    }

    /*
     * 최근 등록된 여행기 조회
     * */
    public GetRecentTraveloguesResponse getRecentTravelogues(final String currentMemberId, final int page) {
        Slice<TravelogueDTO> travelogues = travelogueRepository.findOrderByCreatedAt(currentMemberId, generatePageDesc(page, LARGE_PAGE_SIZE, "createdAt"));

        return GetRecentTraveloguesResponse.of(
                LARGE_PAGE_SIZE,
                page,
                travelogues.getNumberOfElements(),
                travelogues.hasNext(),
                travelogues.getContent()
        );
    }

    /*
     * 인기있는 여행기 조회
     * */
    public GetHotTraveloguesResponse getHotTravelogue(String currentMemberId, int lastLikeCount, int size) {
        return travelogueRepository.findOrderByLikeCount(currentMemberId, lastLikeCount, size);
    }

    /*
     * 내 특정 여행기 삭제
     * */
    @Transactional
    public void deleteMyTravelogue(final String currentMemberId, final Long travelogueId) {
        Travelogue travelogue = travelogueRepository.findByTravelogueIdAndMemberId(travelogueId, currentMemberId)
                .orElseThrow(() -> new TravelogueNotFoundException(travelogueId));

        List<TravelogueDay> travelogueDays = travelogueDayRepository.findByTravelogueIdOrderByDay(travelogueId)
                .orElse(Collections.emptyList());
        if (!travelogueDays.isEmpty()) {
            for (TravelogueDay travelogueDay : travelogueDays) {
                travelogueDayPlaceReviewRepository.deleteAllByTravelogueDayId(travelogueDay.getTravelogueDayId());
            }
            travelogueDayRepository.deleteAll(travelogueDays);
        }

        List<TravelogueImage> travelogueImages = travelogueImageRepository.findByTravelogueId(travelogueId)
                .orElse(Collections.emptyList());
        if (!travelogueImages.isEmpty()) {
            travelogueImageRepository.deleteAll(travelogueImages);
        }

        travelogueRepository.delete(travelogue);
    }
}

package com.adregamdi.travelogue.application;

import com.adregamdi.block.exception.BlockException;
import com.adregamdi.block.infrastructure.BlockRepository;
import com.adregamdi.core.constant.ContentType;
import com.adregamdi.like.application.LikesService;
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
import java.util.stream.IntStream;

import static com.adregamdi.core.constant.Constant.LARGE_PAGE_SIZE;
import static com.adregamdi.core.utils.PageUtil.generatePageDesc;
import static com.adregamdi.media.domain.ImageTarget.TRAVELOGUE;

@Slf4j
@RequiredArgsConstructor
@Service
public class TravelogueServiceImpl implements TravelogueService {
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
     * [여행기 등록]
     * */
    @Override
    @Transactional
    public CreateMyTravelogueResponse createMyTravelogue(final String currentMemberId, final CreateMyTravelogueRequest request) {
        Travel travel = travelRepository.findById(request.travelId())
                .orElseThrow(() -> new TravelException.TravelNotFoundException(request.travelId()));

        validateTravelEnded(travel);

        Travelogue travelogue = request.travelogueId() == null
                ? createNewTravelogue(currentMemberId, request)
                : updateExistingTravelogue(request);

        List<CreateMyTravelogueRequest.TravelogueImageInfo> requestImages = getRequestImages(request);
        saveTravelogueImages(requestImages, travelogue.getTravelogueId());

        if (request.travelogueId() == null) {
            saveTravelogueDaysAndReviews(request.dayList(), travelogue.getTravelogueId());
        } else {
            updateTravelogueDaysAndReviews(request, travelogue.getTravelogueId());
        }

        return CreateMyTravelogueResponse.from(travelogue.getTravelogueId());
    }

    private void validateTravelEnded(final Travel travel) {
        LocalDate today = LocalDate.now();
        if (travel.getEndDate().isAfter(today) || travel.getEndDate().isEqual(today)) {
            throw new TravelogueException.TravelNotEndedException();
        }
    }

    private Travelogue createNewTravelogue(final String currentMemberId, final CreateMyTravelogueRequest request) {
        Travelogue existingTravelogue = travelogueRepository.findByTravelId(request.travelId());
        if (existingTravelogue != null) {
            throw new TravelogueExistException(request.travelId());
        }
        return travelogueRepository.save(new Travelogue(currentMemberId, request.travelId(), request.title(), request.introduction()));
    }

    private Travelogue updateExistingTravelogue(final CreateMyTravelogueRequest request) {
        Travelogue travelogue = findTravelogueById(request.travelogueId());
        travelogue.update(request.title(), request.introduction());
        return travelogue;
    }

    private Travelogue findTravelogueById(final Long travelogueId) {
        return travelogueRepository.findById(travelogueId)
                .orElseThrow(() -> new TravelogueNotFoundException(travelogueId));
    }

    private List<CreateMyTravelogueRequest.TravelogueImageInfo> getRequestImages(final CreateMyTravelogueRequest request) {
        if (request.travelogueImageList() == null || request.travelogueImageList().isEmpty()) {
            return Collections.singletonList(new CreateMyTravelogueRequest.TravelogueImageInfo("https://adregamdi-dev2.s3.ap-northeast-2.amazonaws.com/profile/default_profile_image.png"));
        }
        return request.travelogueImageList();
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

    private void saveOrUpdateImages(final Long travelogueId, final List<CreateMyTravelogueRequest.TravelogueImageInfo> travelogueImages) {
        List<String> urls = travelogueImages.stream()
                .map(CreateMyTravelogueRequest.TravelogueImageInfo::url)
                .toList();

        imageService.saveTargetId(urls, TRAVELOGUE, String.valueOf(travelogueId));
    }

    private void saveTravelogueDaysAndReviews(
            final List<CreateMyTravelogueRequest.DayInfo> dayList,
            final Long travelogueId
    ) {
        Optional.ofNullable(dayList).orElse(Collections.emptyList()).forEach(dayInfo -> {
            TravelogueDay travelogueDay = travelogueDayRepository.save(TravelogueDay.builder()
                    .travelogueId(travelogueId)
                    .date(dayInfo.date())
                    .day(dayInfo.day())
                    .content(dayInfo.content())
                    .memo(dayInfo.memo())
                    .build());
            saveTravelogueDayPlaceReview(dayInfo.placeList(), travelogueDay.getTravelogueDayId());
        });
    }

    private void saveTravelogueDayPlaceReview(
            final List<CreateMyTravelogueRequest.PlaceInfo> places,
            final Long travelogueDayId
    ) {
        Optional.ofNullable(places).orElse(Collections.emptyList()).forEach(placeInfo ->
                travelogueDayPlaceReviewRepository.save(TravelogueDayPlaceReview.builder()
                        .travelogueDayId(travelogueDayId)
                        .placeId(placeInfo.placeId())
                        .placeReviewId(placeInfo.placeReviewId())
                        .build())
        );
    }

    private void updateTravelogueDaysAndReviews(final CreateMyTravelogueRequest request, final Long travelogueId) {
        List<TravelogueDay> travelogueDays = travelogueDayRepository.findByTravelogueIdOrderByDay(travelogueId);
        List<CreateMyTravelogueRequest.DayInfo> days = Optional.ofNullable(request.dayList()).orElse(Collections.emptyList());

        IntStream.range(0, travelogueDays.size())
                .forEach(i -> updateTravelogueDay(travelogueDays.get(i), days.size() > i ? days.get(i) : null));
    }

    private void updateTravelogueDay(final TravelogueDay travelogueDay, final CreateMyTravelogueRequest.DayInfo dayInfo) {
        if (dayInfo == null) return;

        List<TravelogueDayPlaceReview> reviews = travelogueDayPlaceReviewRepository.findByTravelogueDayId(travelogueDay.getTravelogueDayId());
        List<CreateMyTravelogueRequest.PlaceInfo> placeInfos = Optional.ofNullable(dayInfo.placeList()).orElse(Collections.emptyList());

        IntStream.range(0, reviews.size())
                .forEach(j -> {
                    if (reviews.get(j).getPlaceReviewId() == null && placeInfos.size() > j) {
                        reviews.get(j).update(
                                reviews.get(j).getTravelogueDayId(),
                                reviews.get(j).getPlaceId(),
                                placeInfos.get(j).placeReviewId());
                    }
                });
    }

    /*
     * [특정 여행기 조회]
     */
    @Override
    @Transactional(readOnly = true)
    public GetTravelogueResponse get(final String currentMemberId, final Long travelogueId) {
        Travelogue travelogue = findTravelogueById(travelogueId);
        Member member = findMemberById(travelogue.getMemberId());

        validateNotBlocked(currentMemberId, travelogue.getMemberId());

        List<TravelogueImage> travelogueImages = travelogueImageRepository.findByTravelogueId(travelogueId);
        List<TravelogueDay> travelogueDays = travelogueDayRepository.findByTravelogueIdOrderByDay(travelogueId);

        Map<Long, List<GetTravelogueResponse.PlaceInfo>> placeReviewsMap = createPlaceReviewsMap(travelogueDays);
        boolean isLiked = likesService.checkIsLiked(currentMemberId, ContentType.TRAVELOGUE, travelogueId);

        return GetTravelogueResponse.of(isLiked, member, travelogue, travelogueImages, travelogueDays, placeReviewsMap);
    }

    private Member findMemberById(final String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException.MemberNotFoundException(memberId));
    }

    private void validateNotBlocked(final String currentMemberId, final String travelogueMemberId) {
        blockRepository.findByBlockedMemberIdAndBlockingMemberId(travelogueMemberId, currentMemberId)
                .ifPresent(block -> {
                    throw new BlockException.BlockExistException(block);
                });
    }

    private Map<Long, List<GetTravelogueResponse.PlaceInfo>> createPlaceReviewsMap(final List<TravelogueDay> travelogueDays) {
        return travelogueDays.stream()
                .collect(Collectors.toMap(
                        TravelogueDay::getTravelogueDayId,
                        this::createPlaceInfoList,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    private List<GetTravelogueResponse.PlaceInfo> createPlaceInfoList(final TravelogueDay travelogueDay) {
        List<TravelogueDayPlaceReview> travelogueDayPlaceReviews = travelogueDayPlaceReviewRepository
                .findByTravelogueDayId(travelogueDay.getTravelogueDayId());

        return travelogueDayPlaceReviews.stream()
                .map(this::createPlaceInfo)
                .collect(Collectors.toList());
    }

    private GetTravelogueResponse.PlaceInfo createPlaceInfo(final TravelogueDayPlaceReview tdpr) {
        Place place = findPlaceById(tdpr.getPlaceId());
        PlaceReview placeReview = findPlaceReview(tdpr.getPlaceReviewId());
        List<PlaceReviewImage> images = placeReviewImageRepository
                .findByPlaceReviewIdOrderByPlaceReviewImageIdDesc(placeReview.getPlaceReviewId());

        return GetTravelogueResponse.PlaceInfo.builder()
                .placeId(place.getPlaceId())
                .placeReviewId(placeReview.getPlaceReviewId())
                .title(place.getTitle())
                .contentsLabel(place.getContentsLabel())
                .regionLabel(place.getRegionLabel())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .content(placeReview.getContent())
                .placeReviewImageList(createPlaceReviewImageInfoList(images))
                .build();
    }

    private Place findPlaceById(final Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceException.PlaceNotFoundException(placeId));
    }

    private PlaceReview findPlaceReview(final Long placeReviewId) {
        if (placeReviewId == null) {
            return PlaceReview.builder().build();
        }
        return placeReviewRepository.findById(placeReviewId)
                .orElseThrow(() -> new PlaceException.PlaceReviewNotFoundException(placeReviewId));
    }

    private List<GetTravelogueResponse.PlaceReviewImageInfo> createPlaceReviewImageInfoList(final List<PlaceReviewImage> images) {
        return images.stream()
                .map(img -> new GetTravelogueResponse.PlaceReviewImageInfo(img.getUrl()))
                .collect(Collectors.toList());
    }

    /*
     * [특정 회원 전체 여행기 조회]
     */
    @Override
    @Transactional(readOnly = true)
    public GetMemberTraveloguesResponse getMemberTravelogues(final String memberId, final Long lastTravelogueId, final int size) {
        return travelogueRepository.findMemberTravelogues(memberId, lastTravelogueId, size);
    }

    /*
     * [내 전체 여행기 조회]
     * */
    @Override
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
     * [최근 등록된 여행기 조회]
     * */
    @Override
    @Transactional(readOnly = true)
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
     * [인기있는 여행기 조회]
     * */
    @Override
    @Transactional(readOnly = true)
    public GetHotTraveloguesResponse getHotTravelogues(String currentMemberId, int lastLikeCount, int size) {
        return travelogueRepository.findOrderByLikeCount(currentMemberId, lastLikeCount, size);
    }

    /*
     * [내 특정 여행기 삭제]
     * */
    @Override
    @Transactional
    public void deleteMyTravelogue(final String currentMemberId, final Long travelogueId) {
        Travelogue travelogue = travelogueRepository.findByTravelogueIdAndMemberId(travelogueId, currentMemberId)
                .orElseThrow(() -> new TravelogueNotFoundException(travelogueId));
        deleteTravelogue(travelogue);
    }

    /*
     * [특정 회원의 여행기 전체 삭제]
     * */
    @Override
    @Transactional
    public void deleteMyTravelogue(final String memberId) {
        List<Travelogue> travelogues = travelogueRepository.findAllByMemberId(memberId);
        travelogues.forEach(this::deleteTravelogue);
    }

    private void deleteTravelogue(final Travelogue travelogue) {
        deleteTravelogueDays(travelogue.getTravelogueId());
        deleteTravelogueImages(travelogue.getTravelogueId());
        travelogueRepository.delete(travelogue);
    }

    private void deleteTravelogueDays(final Long travelogueId) {
        List<TravelogueDay> travelogueDays = travelogueDayRepository.findByTravelogueIdOrderByDay(travelogueId);
        travelogueDays.forEach(this::deleteTravelogueDay);
        travelogueDayRepository.deleteAll(travelogueDays);
    }

    private void deleteTravelogueDay(final TravelogueDay travelogueDay) {
        travelogueDayPlaceReviewRepository.deleteAllByTravelogueDayId(travelogueDay.getTravelogueDayId());
    }

    private void deleteTravelogueImages(final Long travelogueId) {
        List<TravelogueImage> travelogueImages = travelogueImageRepository.findByTravelogueId(travelogueId);
        travelogueImageRepository.deleteAll(travelogueImages);
    }
}

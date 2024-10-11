package com.adregamdi.shorts.application;

import com.adregamdi.like.application.LikesService;
import com.adregamdi.like.domain.enumtype.ContentType;
import com.adregamdi.media.application.FileUploadService;
import com.adregamdi.member.dto.ShortsWithMemberDTO;
import com.adregamdi.place.application.PlaceService;
import com.adregamdi.place.domain.Place;
import com.adregamdi.place.dto.response.GetPlaceResponse;
import com.adregamdi.shorts.domain.Shorts;
import com.adregamdi.shorts.dto.ShortsDTO;
import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.request.GetShortsByPlaceIdRequest;
import com.adregamdi.shorts.dto.request.UpdateShortsRequest;
import com.adregamdi.shorts.dto.response.*;
import com.adregamdi.shorts.exception.ShortsException;
import com.adregamdi.shorts.infrastructure.ShortsRepository;
import com.adregamdi.travelogue.application.TravelogueService;
import com.adregamdi.travelogue.dto.response.GetTravelogueResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ShortsServiceImpl implements ShortsService {
    private final ShortsRepository shortsRepository;
    private final FileUploadService fileUploadService;
    private final ShortsValidService shortsValidService;
    private final PlaceService placeService;
    private final TravelogueService travelogueService;
    private final LikesService likesService;

    @Override
    public GetShortsByShortsIdResponse getShortsByShortsId(final String currentMemberId, final Long shortsId) {
        ShortsWithMemberDTO shortsWithMember = shortsRepository.findShortsWithMemberByShortsId(shortsId)
                .map(this::mapToShortsWithMemberDTO)
                .orElseThrow(() -> new ShortsException.ShortsNotFoundException(shortsId));
        GetPlaceResponse place = GetPlaceResponse.builder().place(Place.builder().build()).build();
        if (shortsWithMember.placeId() != null) {
            place = placeService.get(currentMemberId, shortsWithMember.placeId());
        }
        GetTravelogueResponse travelogue = GetTravelogueResponse.builder().travelogueImageList(Collections.emptyList()).build();
        if (shortsWithMember.travelogueId() != null) {
            travelogue = travelogueService.get(currentMemberId, shortsWithMember.travelogueId());
        }
        Integer likeCount = likesService.getLikesCount(ContentType.SHORTS, shortsId);
        Boolean isLiked = likesService.checkIsLiked(currentMemberId, ContentType.SHORTS, shortsId);
        return GetShortsByShortsIdResponse.from(ShortsDTO.builder()
                .shortsId(shortsWithMember.shortsId())
                .title(shortsWithMember.title())
                .memberId(shortsWithMember.memberId())
                .name(shortsWithMember.memberName())
                .handle(shortsWithMember.memberHandle())
                .profile(shortsWithMember.memberProfile())
                .placeId(shortsWithMember.placeId())
                .placeTitle(place.place().getTitle() == null ? "" : place.place().getTitle())
                .placeImage(place.place().getImgPath() == null ? "" : place.place().getImgPath())
                .travelogueId(shortsWithMember.travelogueId())
                .travelogueTitle(travelogue.title())
                .travelogueImage(travelogue.travelogueImageList().isEmpty() ? "" : travelogue.travelogueImageList().get(0).url())
                .shortsVideoUrl(shortsWithMember.shortsVideoUrl())
                .thumbnailUrl(shortsWithMember.thumbnailUrl())
                .viewCount(shortsWithMember.viewCount())
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build());
    }

    private ShortsWithMemberDTO mapToShortsWithMemberDTO(Object[] result) {
        if (result == null || result.length == 0 || !(result[0] instanceof Object[] data)) {
            throw new IllegalArgumentException("Unexpected result format");
        }

        if (data.length < 12) {
            throw new IllegalArgumentException("Insufficient data in result");
        }

        return new ShortsWithMemberDTO(
                toLong(data[0]),
                toString(data[1]),
                toString(data[2]),
                toLong(data[3]),
                toLong(data[4]),
                toString(data[5]),
                toString(data[6]),
                toBoolean(data[7]),
                toInt(data[8]),
                toString(data[9]),
                toString(data[10]),
                toString(data[11])
        );
    }

    private Long toLong(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        return null;
    }

    private String toString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    private Boolean toBoolean(Object obj) {
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue() != 0;
        }
        return null;
    }

    private Integer toInt(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        return null;
    }

    @Override
    public GetShortsResponse getShorts(String memberId, long lastId, int size) {
        return shortsRepository.getShortsForMember(memberId, lastId, size);
    }

    @Override
    public GetShortsResponse getUserShorts(String memberId, long lastShortsId, int size) {
        return shortsRepository.getUserShorts(memberId, lastShortsId, size);
    }

    @Override
    public GetShortsResponse getHotShorts(String memberId, int lastLikeCount, int size) {
        return shortsRepository.getHotShorts(memberId, lastLikeCount, size);
    }

    @Override
    public GetShortsByPlaceIdResponse getShortsByPlaceId(String memberId, GetShortsByPlaceIdRequest request) {
        return shortsRepository.getShortsByPlaceId(memberId, request);
    }

    @Override
    public String getS3KeyByShortId(Long shortsId) {
        Shorts shorts = shortsRepository.findById(shortsId)
                .orElseThrow(() -> new ShortsException.ShortsNotFoundException(shortsId));

        return fileUploadService.extractKeyFromUrl(shorts.getShortsVideoUrl());
    }

    @Override
    public SaveVideoResponse saveVideo(UploadVideoDTO videoUrls, String memberId) {

        Shorts savedShorts = shortsRepository.save(
                Shorts.builder()
                        .memberId(memberId)
                        .shortsVideoUrl(videoUrls.getVideoUrl())
                        .thumbnailUrl(videoUrls.getVideoThumbnailUrl())
                        .build()
        );

        log.info("비디오가 저장되었습니다.: shorts ID: {}, videoUrl: {}, thumbnail: {}", savedShorts.getShortsId(), savedShorts.getShortsVideoUrl(), savedShorts.getThumbnailUrl());
        return SaveVideoResponse.ofEntity(savedShorts);
    }

    @Override
    public void saveShorts(final String memberId, final CreateShortsRequest request) {

        Shorts unAssignedShorts = shortsRepository.findById(request.shortsId())
                .orElseThrow(() -> new ShortsException.ShortsNotFoundException(request.shortsId()));
        unAssignedShorts.assign(request);

        log.info("Shorts saved and assigned. shortsId: {}", unAssignedShorts.getShortsId());
    }


    @Override
    public void updateShorts(String memberId, UpdateShortsRequest request) {

        Shorts savedShorts = shortsRepository.findById(request.shortsId())
                .orElseThrow(() -> new ShortsException.ShortsNotFoundException(request.shortsId()));

        if (!shortsValidService.isWriter(memberId, savedShorts.getMemberId())) {
            log.warn("Invalid Member Request. memberId: {}", memberId);
            throw new ShortsException.ShortsNOTWRITERException(memberId);
        }

        // 기존 비디오 & 썸네일 스토리지 내에서 삭제
//        fileUploadService.deleteFile(savedShorts.getShortsVideoUrl());
//        fileUploadService.deleteFile(savedShorts.getThumbnailUrl());

        savedShorts.update(request);

        log.info("Shorts updated. shortsId: {}", savedShorts.getShortsId());
    }

    @Override
    public void deleteShorts(String memberId, Long shortsId) {

        Shorts savedShorts = shortsRepository.findById(shortsId)
                .orElseThrow(() -> {
                    log.error("요청한 ID 값의 엔티티가 존재하지 않습니다. shortsId: {}", shortsId);
                    return new ShortsException.ShortsNotFoundException(shortsId);
                });

        if (!shortsValidService.isWriter(memberId, savedShorts.getMemberId())) {
            log.warn("Invalid Member Request. memberId: {}", memberId);
            throw new ShortsException.ShortsNOTWRITERException(memberId);
        }

        // 비디오 & 썸네일 스토리지 내에서 삭제
        fileUploadService.deleteFile(savedShorts.getShortsVideoUrl());
        fileUploadService.deleteFile(savedShorts.getThumbnailUrl());

        shortsRepository.delete(savedShorts);
    }

    @Override
    public void deleteMyShorts(String memberId) {
        List<Shorts> savedShorts = shortsRepository.findAllByMemberId(memberId);
        if (savedShorts.isEmpty()) {
            return;
        }

        for (Shorts shorts : savedShorts) {
            // 비디오 & 썸네일 스토리지 내에서 삭제
            fileUploadService.deleteFile(shorts.getShortsVideoUrl());
            fileUploadService.deleteFile(shorts.getThumbnailUrl());

            shortsRepository.delete(shorts);
        }
    }

    @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 실행
    private void deleteUnassignedVideo() {

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7); // 일주일 동안 할당되지 않은 이미지 삭제
        log.info("할당되지 않은 비디오 정리를 시작합니다.");
        List<Shorts> unAssignedShortsList = shortsRepository.findUnassignedBeforeDate(cutoffDate);

        // S3에서 이미지 삭제
        for (Shorts shorts : unAssignedShortsList) {
            fileUploadService.deleteFile(shorts.getShortsVideoUrl());
            fileUploadService.deleteFile(shorts.getThumbnailUrl());
            shortsRepository.delete(shorts);
        }

        log.info("DB와 Storage 에서 {}개의 이미지가 삭제되었습니다.", unAssignedShortsList.size());
    }
}

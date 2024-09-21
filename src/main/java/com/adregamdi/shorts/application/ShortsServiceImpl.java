package com.adregamdi.shorts.application;

import com.adregamdi.media.application.FileUploadService;
import com.adregamdi.shorts.domain.Shorts;
import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.request.GetShortsByPlaceIdRequest;
import com.adregamdi.shorts.dto.request.UpdateShortsRequest;
import com.adregamdi.shorts.dto.response.GetShortsByPlaceIdResponse;
import com.adregamdi.shorts.dto.response.GetShortsResponse;
import com.adregamdi.shorts.dto.response.SaveVideoResponse;
import com.adregamdi.shorts.dto.response.UploadVideoDTO;
import com.adregamdi.shorts.exception.ShortsException;
import com.adregamdi.shorts.infrastructure.ShortsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ShortsServiceImpl implements ShortsService {

    private final ShortsRepository shortsRepository;
    private final FileUploadService fileUploadService;

    private final ShortsValidService shortsValidService;

    @Override
    public GetShortsResponse getShorts(String memberId, long lastId, int size) {
        return shortsRepository.getShortsForMember(memberId, lastId, size);
    }

    @Override
    public GetShortsResponse getUserShorts(String memberId, long lastShortsId, int size) {
        return shortsRepository.getUserShorts(memberId, lastShortsId, size);
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

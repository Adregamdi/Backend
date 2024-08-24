package com.adregamdi.media.application;

import com.adregamdi.core.service.FileUploadService;
import com.adregamdi.media.domain.Video;
import com.adregamdi.media.enumtype.MediaType;
import com.adregamdi.media.infrastructure.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
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
public class MediaService {

    private final FileUploadService fileUploadService;
    private final VideoRepository videoRepository;

    public void saveVideo(String url, MediaType mediaType) {
        Video video = Video.builder()
                .url(url)
                .mediaType(mediaType)
                .build();
        videoRepository.save(video);
        log.info("비디오가 저장되었습니다.: shorts ID: {} and media type: {}", url, mediaType);
    }

    public void assignVideo(String url, Long shortsId, MediaType mediaType) {

        Video video = videoRepository.findByUrlAndMediaType(url, mediaType)
                .orElseThrow(EntityNotFoundException::new);

        video.updateTargetId(shortsId);
        log.info("비디오가 할당되었습니다. shorts ID: {} with media type: {}", shortsId, mediaType);
    }

    @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 실행
    public void deleteUnassignedVideo() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7); // 일주일 동안 할당되지 않은 이미지 삭제
        log.info("{}보다 오래된 할당되지 않은 비디오 정리를 시작합니다.", cutoffDate);
        List<String> unassignedUrlList = videoRepository.findUnassignedBeforeDate(cutoffDate);

        // S3에서 이미지 삭제
        for (String url : unassignedUrlList) {
            fileUploadService.deleteFile(url);
        }

        int deletedCount = videoRepository.deleteAllByUrlIn(unassignedUrlList);
        log.info("DB와 Storage 에서 {}개의 이미지가 삭제되었습니다.", deletedCount);
    }



}

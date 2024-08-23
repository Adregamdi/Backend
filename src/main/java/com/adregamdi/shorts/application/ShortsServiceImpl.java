package com.adregamdi.shorts.application;

import com.adregamdi.core.service.FileUploadService;
import com.adregamdi.shorts.domain.Shorts;
import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.response.CreateShortsResponse;
import com.adregamdi.shorts.infrastructure.ShortsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortsServiceImpl implements ShortsService{

    private FileUploadService fileUploadService;
    private final ShortsRepository shortsRepository;


//    private final PlaceService placeService;
//    private final TravelReviewService travelReviewService;


    //  저장 경로 - shorts/memberId(UUID)/uuid_생성날짜.mov

    @Transactional
    public CreateShortsResponse uploadShorts(MultipartFile videoFile, UUID memberId, CreateShortsRequest request) {
        String shortsDirName = "shorts/" + memberId;
        String thumbnailDirName = "shorts/thumbnails/" + memberId;
        String videoKey = fileUploadService.buildKey(videoFile, shortsDirName);
        String thumbnailKey = fileUploadService.buildKey(videoFile, thumbnailDirName);

        try {
            byte[] compressedVideo = MediaUtil.compressVideo(videoFile);
            byte[] thumbnailImage = MediaUtil.generateThumbnail(compressedVideo);


            String videoUrl = fileUploadService.uploadFile(compressedVideo, videoKey);
            String thumbnailUrl = fileUploadService.uploadFile(thumbnailImage, thumbnailKey);

            Shorts savedShorts = shortsRepository.save(Shorts.builder()
                    .title(request.title())
                    .memberId(memberId)
                    .placeNo(request.placeNo())
                    .travelReviewNo(request.travelReviewNo())
                    .shortsVideoUrl(videoUrl)
                    .build());

//            PlaceDTO place = placeService.getPlaceById(savedShorts.getPlaceNo());
//            TravelReviewDTO travelReview =  travelReviewService.getTravelReviewById(savedShorts.getTravelReviewNo());

            return CreateShortsResponse.of(savedShorts);

        } catch (IOException | EncoderException e) {
            log.error("영상 업로드 실패");
            throw new RuntimeException("영상 업로드에 실패하였습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<Shorts> getAllShorts() {
        return shortsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Shorts getShorts(Long shortsId) {
        return shortsRepository.findById(shortsId)
                .orElseThrow(() -> new EntityNotFoundException("Shorts not found with id: " + shortsId));
    }

    @Transactional
    public void deleteShorts(Long shortsId) {
        Shorts shorts = getShorts(shortsId);
        String key = extractKeyFromUrl(shorts.getShortsVideoUrl());
        mediaUtil.deleteFile(key);
        shortsRepository.delete(shorts);
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.indexOf("/shorts"));
    }

}

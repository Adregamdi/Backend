package com.adregamdi.shorts.application;

import com.adregamdi.core.utils.FileStorageUtil;
import com.adregamdi.shorts.domain.Shorts;
import com.adregamdi.shorts.dto.request.CreateShortsRequest;
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

    private final FileStorageUtil fileStorageUtil;
    private final ShortsRepository shortsRepository;


    //  저장 경로 - memberId / UUID + 업로드 시.확장자

    @Transactional
    public Shorts uploadShorts(MultipartFile videoFile, UUID memberId, CreateShortsRequest request) {
        String dirName = "shorts/" + memberId;

        try {
            String videoUrl = fileStorageUtil.uploadVideo(videoFile, dirName);

            Shorts shorts = Shorts.builder()
                    .title(request.title())
                    .memberId(memberId)
                    .placeNo(request.placeNo())
                    .travelReviewNo(request.travelReviewNo())
                    .build();

            return shortsRepository.save(shorts);
        } catch (IOException | EncoderException e) {
            log.error("영상 업로드 실패");
            throw new RuntimeException("영상 업로드에 실패하였습니다.", e);
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
        String key = extractKeyFromUrl(shorts.getVideoUrl());
        fileStorageUtil.deleteFile(key);
        shortsRepository.delete(shorts);
    }

    private String extractKeyFromUrl(String url) {
        // S3 URL에서 키를 추출하는 로직
        // 예: https://your-bucket.s3.amazonaws.com/shorts/123/video.mp4 에서 shorts/123/video.mp4 추출
        return url.substring(url.indexOf("/shorts"));
    }
//    @Override
//    @Transactional
//    public String uploadVideo(MultipartFile video, UUID memberId) {
//
//        String dirName = String.valueOf(memberId);
//
//        String videoUrl = null;
//        try {
//            String key = buildKey(dirName, Objects.requireNonNull(video.getOriginalFilename()));
//            videoUrl = putVideoToS3(key, video);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (ResponseStatusException e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드에 실패하였습니다.");
//        }
//
//        return videoUrl;
//    }
//
//    private String putVideoToS3(String key, MultipartFile videoFile) throws IOException {
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setContentType(videoFile.getContentType());
//        objectMetadata.setContentLength(videoFile.getSize());
//
//        amazonS3Client.putObject(bucketName, key, videoFile.getInputStream(), objectMetadata);
//
//        return amazonS3Client.getUrl(bucketName, key).toString();
//    }
//
//    /**
//     * 비디오 경로와 파일 이름을 설정하는 메소드입니다.
//     * @param dirName 저장 경로 이름입니다.
//     * @param fileName 저장되는 파일 명입니다.
//     * @return key가 생성됩니다.
//     */
//    private String buildKey(String dirName, String fileName) {
//
//        String extension = validateService.checkVideoFile(fileName);   // 확장자 비교 후 반환
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String now = sdf.format(new Date());
//
//        String newFileName = UUID.randomUUID() + "_" + now;
//
//        return dirName + "/" + newFileName + "." + extension;
//    }

}

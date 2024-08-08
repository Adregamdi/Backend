package com.adregamdi.shorts.application;

import com.adregamdi.shorts.infrastructure.ShortsRepository;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShortsServiceImpl implements ShortsService{

    private final AmazonS3Client amazonS3Client;
    private final ShortsValidateService validateService;
    private final ShortsRepository shortsRepository;

    @Value("cloud.aws.s3.bucket")
    private String bucketName;

    //  저장 경로 - memberId / UUID + 업로드 시.확장자

    @Override
    @Transactional
    public String uploadVideo(MultipartFile video, UUID memberId) {

        String dirName = String.valueOf(memberId);

        String videoUrl = null;
        try {
            String key = buildKey(dirName, Objects.requireNonNull(video.getOriginalFilename()));
            videoUrl = putVideoToS3(key, video);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드에 실패하였습니다.");
        }

        return videoUrl;
    }

    private String putVideoToS3(String key, MultipartFile videoFile) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(videoFile.getContentType());
        objectMetadata.setContentLength(videoFile.getSize());

        amazonS3Client.putObject(bucketName, key, videoFile.getInputStream(), objectMetadata);

        return amazonS3Client.getUrl(bucketName, key).toString();
    }

    /**
     * 비디오 경로와 파일 이름을 설정하는 메소드입니다.
     * @param dirName 저장 경로 이름입니다.
     * @param fileName 저장되는 파일 명입니다.
     * @return key가 생성됩니다.
     */
    private String buildKey(String dirName, String fileName) {

        String extension = validateService.checkVideoFile(fileName);   // 확장자 비교 후 반환

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String now = sdf.format(new Date());

        String newFileName = UUID.randomUUID() + "_" + now;

        return dirName + "/" + newFileName + "." + extension;
    }

}

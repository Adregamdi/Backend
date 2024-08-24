package com.adregamdi.media.application;

import com.adregamdi.core.service.FileUploadService;
import com.adregamdi.media.dto.response.UploadVideoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.MultimediaInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final FileUploadService fileUploadService;
    private final VideoValidateService videoValidateService;

    private final static int VIDEO_MAX_BITRATE = 1000000; // 1Mbps
    private final static int VIDEO_MAX_DURATION = 60; // 60 seconds

    public String getEncodedFileName(String key) {
        try {
            return URLEncoder.encode(key, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        } catch (Exception e) {
            log.error("Failed to encode file name: {}", key, e);
            throw new RuntimeException("파일 인코딩 과정에서 에러가 발생하였습니다.", e);
        }
    }

    public byte[] compressVideo(MultipartFile videoFile) throws IOException, EncoderException {
        File source = fileUploadService.convertMultipartFileToFile(videoFile);
        File target = File.createTempFile("compressed", "." + fileUploadService.extractFileExtension(videoFile));

        MultimediaObject multimediaObject = new MultimediaObject(source);
        MultimediaInfo info = multimediaObject.getInfo();

        if (info.getDuration() > VIDEO_MAX_DURATION * 1000) {
            throw new IllegalArgumentException("쇼츠는 60초를 넘길 수 없습니다!");
        }

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("aac");
        audio.setBitRate(128000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);

        VideoAttributes video = new VideoAttributes();
        video.setCodec("h264");
        video.setBitRate(VIDEO_MAX_BITRATE);
        video.setFrameRate(30);

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setOutputFormat("mp4");
        attrs.setAudioAttributes(audio);
        attrs.setVideoAttributes(video);

        Encoder encoder = new Encoder();
        encoder.encode(multimediaObject, target, attrs);

        source.delete();
        target.delete();

        log.info("성공적으로 비디오가 압축되었습니다.: {}", videoFile.getOriginalFilename());
        return Files.readAllBytes(target.toPath());


    }

    public byte[] generateThumbnail(byte[] videoBytes) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(videoBytes);
             FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(inputStream)) {

            frameGrabber.start();
            Frame frame = frameGrabber.grabImage();
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage thumbnail = converter.getBufferedImage(frame);
            frameGrabber.stop();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "png", outputStream);
            log.info("성공적으로 썸네일이 제작되었습니다.");
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("썸네일 제작 중 에러가 발생하였습니다.", e);
            throw new RuntimeException("썸네일 제작 과정에서 에러가 발생하였습니다.", e);
        }
    }

    public UploadVideoResponse uploadVideo(MultipartFile videoFile, UUID memberId) throws EncoderException {
        try {
            log.info("비디오 업로드를 시작합니다. memberId: {}", memberId);
            videoValidateService.checkVideoFile(videoFile.getOriginalFilename());

            byte[] compressedVideo = compressVideo(videoFile);
            byte[] thumbnail = generateThumbnail(compressedVideo);

            String videoKey = buildKey(videoFile, memberId.toString(), "shorts");
            String thumbnailKey = buildKey(videoFile, memberId.toString(), "thumbnails");

            String uploadedVideoUrl = fileUploadService.uploadFile(compressedVideo, videoKey);
            String videoThumbnailUrl = fileUploadService.uploadFile(thumbnail, thumbnailKey);

            log.info("성공적으로 비디오가 업로드되었습니다. memberId: {}", memberId);
            return new UploadVideoResponse(uploadedVideoUrl, videoThumbnailUrl);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 형식의 비디오입니다. file: {} for memberId: {}", videoFile.getOriginalFilename(), memberId);
            throw e;
        } catch (IOException e) {
            log.error("비디오 업로드 중 에러가 발생하였습니다. for member: {}", memberId, e);
            throw new RuntimeException("비디오 파일 업로드 중에 에러가 발생하였습니다.", e);
        }
    }

    private String buildKey(MultipartFile file, String additionalDir, String type) {
        String dirName = type + "/" + additionalDir;
        return fileUploadService.buildKey(file, dirName);
    }

}

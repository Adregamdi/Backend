package com.adregamdi.media.application;

import com.adregamdi.core.service.FileUploadService;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class VideoService implements MediaService{

    private FileUploadService fileUploadService;

    private final static int VIDEO_MAX_BITRATE = 1000000; // 1Mbps
    private final static int VIDEO_MAX_DURATION = 60; // 60 seconds

    public String getEncodedFileName(String key) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }

    public byte[] compressVideo(MultipartFile videoFile) throws IOException, EncoderException {
        File source = fileUploadService.convertMultipartFileToFile(videoFile);
        File target = File.createTempFile("compressed", "." + fileUploadService.extractFileExtension(videoFile));

        MultimediaObject multimediaObject = new MultimediaObject(source);
        // 동영상 파일 정보 추출
        MultimediaInfo info = multimediaObject.getInfo();

        if (info.getDuration() > VIDEO_MAX_DURATION * 1000) {
            throw new IllegalArgumentException("쇼츠는 60초를 넘길 수 없습니다!");
        }

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("aac");  // AAC 코덱
        audio.setBitRate(128000);   // 128 kbps
        audio.setChannels(2);   // 스테레오 채널
        audio.setSamplingRate(44100);   // 44.1 kHz 샘플링

        VideoAttributes video = new VideoAttributes();
        video.setCodec("h264");
        video.setBitRate(VIDEO_MAX_BITRATE);
        video.setFrameRate(30); // 30 프레임 속도로 인코딩

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setOutputFormat("mp4");
        attrs.setAudioAttributes(audio);
        attrs.setVideoAttributes(video);

        Encoder encoder = new Encoder();
        encoder.encode(multimediaObject, target, attrs);

        byte[] compressedVideo = Files.readAllBytes(target.toPath());

        source.delete();
        target.delete();

        return compressedVideo;
    }

    public byte[] generateThumbnail(byte[] videoBytes) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(videoBytes);
             FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(inputStream)) {

            frameGrabber.start();
            Frame frame = frameGrabber.grabImage();
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage thumbnail = converter.getBufferedImage(frame);
            frameGrabber.stop();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "png", outputStream);
            return outputStream.toByteArray();
        }
    }
}

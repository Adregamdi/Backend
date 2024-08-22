package com.adregamdi.core.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.MultimediaInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileStorageUtil {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final static int IMAGE_RESIZE_TARGET_WIDTH = 650;
    private final static int VIDEO_MAX_BITRATE = 1000000; // 1Mbps
    private final static int VIDEO_MAX_DURATION = 60; // 60 seconds

    public String uploadFile(byte[] fileContent, String key) {
        try {
            return putFileToS3(key, fileContent);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드에 실패하였습니다.");
        }
    }

    public String uploadImage(MultipartFile imageFile, String key) throws IOException {
        byte[] resizedImage = resizeImage(imageFile);
        return uploadFile(resizedImage, key);
    }

    public String uploadVideo(MultipartFile videoFile, String key) throws IOException, EncoderException {
        byte[] compressedVideo = compressVideo(videoFile);
        return uploadFile(compressedVideo, key);
    }

    public byte[] downloadFile(String key) throws IOException {
        S3Object s3Object = amazonS3Client.getObject(bucketName, key);
        S3ObjectInputStream out = s3Object.getObjectContent();
        return IOUtils.toByteArray(out);
    }

    public String getEncodedFileName(String key) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }

    public void moveFileOnS3(String sourceKey, String targetKey) {
        copyFile(sourceKey, targetKey);
        deleteFile(sourceKey);
    }

    public void deleteFile(String key) {
        amazonS3Client.deleteObject(bucketName, key);
    }

    private String putFileToS3(String key, byte[] fileContent) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        objectMetadata.setContentLength(fileContent.length);

        amazonS3Client.putObject(bucketName, key, new ByteArrayInputStream(fileContent), objectMetadata);

        return amazonS3Client.getUrl(bucketName, key).toString();
    }

    public String buildKey(String dirName, String originalFileName) {
        String extension = getFileExtension(originalFileName);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String now = sdf.format(new Date());
        String newFileName = UUID.randomUUID() + "_" + now;
        return dirName + "/" + newFileName + "." + extension;
    }

    public byte[] resizeImage(MultipartFile multipartFile) throws IOException {
        BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());
        BufferedImage resizedImage =
                Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, IMAGE_RESIZE_TARGET_WIDTH, Scalr.THRESHOLD_QUALITY_BALANCED);
        String fileExtension = getFileExtension(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, fileExtension, outputStream);

        return outputStream.toByteArray();
    }

    public byte[] compressVideo(MultipartFile videoFile) throws IOException, EncoderException {
        File source = convertMultipartFileToFile(videoFile);
        File target = File.createTempFile("compressed", "." + getFileExtension(videoFile.getOriginalFilename()));

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

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = File.createTempFile("temp", "." + getFileExtension(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }

    private void copyFile(String sourceKey, String targetKey) {
        amazonS3Client.copyObject(new CopyObjectRequest(bucketName, sourceKey, bucketName, targetKey));
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}

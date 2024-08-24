package com.adregamdi.core.service;

import com.adregamdi.core.utils.SecureStringUtil;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService implements FileUploadService{

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private static final int SECURE_STRING_BYTE_SIZE = 16; // 16 byte -> 영문 + 숫자 조합 22자리

    @Override
    public String buildKey(MultipartFile file, String dirName) {
        String extension = extractFileExtension(file);
        return buildKey(extension, dirName);
    }

    @Override
    public String buildKey(String extension, String dirName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());

        return dirName + "/" +
                SecureStringUtil.buildSecureString(SECURE_STRING_BYTE_SIZE) + "_" + date + extension;
    }

    @Override
    public String uploadFile(MultipartFile file, String key) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
        amazonS3Client.putObject(putObjectRequest);

        return amazonS3Client.getUrl(bucketName, key).toString();
    }

    @Override
    public String uploadFile(byte[] fileContent, String key) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        objectMetadata.setContentLength(fileContent.length);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(fileContent), objectMetadata);
        amazonS3Client.putObject(putObjectRequest);

        return amazonS3Client.getUrl(bucketName, key).toString();
    }

    @Override
    public void copyFile(String sourceKey, String targetKey) {
        amazonS3Client.copyObject(new CopyObjectRequest(bucketName, sourceKey, bucketName, targetKey));
    }

    @Override
    public void moveFileOnStorage(String sourceKey, String targetKey) {
        copyFile(sourceKey, targetKey);
        deleteFile(sourceKey);
    }

    @Override
    public void deleteFile(String url) {
        try {
            String key = extractKeyFromUrl(url);
            amazonS3Client.deleteObject(bucketName, key);
        } catch (AmazonServiceException e) {
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    @Override
    public String extractKeyFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String path = url.getPath();

            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            if (path.isEmpty()) {
                throw new IllegalArgumentException("URL 내에 키 값이 포함되어야 합니다.");
            }

            return path;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("잘못된 URL입니다.");
        }
    }

    @Override
    public String getEncodedFileName(String key) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }

    @Override
    public byte[] downloadFile(String key) throws IOException {
        S3Object s3Object = amazonS3Client.getObject(bucketName, key);
        S3ObjectInputStream out = s3Object.getObjectContent();
        return IOUtils.toByteArray(out);
    }

    @Override
    public String extractFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        return Objects.requireNonNull(fileName).substring(fileName.lastIndexOf("."));
    }

    /**
     * MultipartFile에서 File로 변경해주는 메소드입니다.
     * 추후에 File은 꼭 삭제해주어야 합니다.
     * @param file 클라이언트로부터 받은 MultipartFile 입니다.
     * @return File 변환된 File입니다.
     * @throws IOException
     */
    @Override
    public File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}

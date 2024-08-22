package com.adregamdi.media.application;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface FileUploadService {

    String buildKey(MultipartFile file, String dirName);
    String uploadFile(MultipartFile file, String key) throws IOException;
    String uploadFile(byte[] fileContent, String key);
    void copyFile(String sourceKey, String targetKey);
    void moveFileOnS3(String sourceKey, String targetKey);
    void deleteFile(String key);
    String extractKeyFromUrl(String url);
    String getEncodedFileName(String key);
    byte[] downloadFile(String key) throws IOException;
    String extractFileExtension(MultipartFile file);
    File convertMultipartFileToFile(MultipartFile file) throws IOException;
}

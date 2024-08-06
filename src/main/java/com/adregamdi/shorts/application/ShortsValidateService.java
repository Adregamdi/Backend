package com.adregamdi.shorts.application;

import org.springframework.stereotype.Component;

@Component
public class ShortsValidateService {

    public String checkVideoFile(String filename) {
        if (isFileNameInvalid(filename)) {
            throw new IllegalArgumentException("파일 이름이 유효하지 않습니다.");
        }

        String extension = getFileExtension(filename);

        if (!isVideoExtension(extension)) {
            throw new IllegalArgumentException("지원하지 않는 비디오 파일 형식입니다.");
        }

        return extension;
    }

    private boolean isFileNameInvalid(String fileName) {
        return fileName == null || fileName.isBlank() || !fileName.contains(".");
    }

    private boolean isVideoExtension(String extension) {
        String[] videoExtensions = {"mp4", "avi", "mov", "wmv", "flv", "mkv", "webm"};

        for (String ext : videoExtensions) {
            if (extension.equalsIgnoreCase(ext)) {
                return true;
            }
        }

        return false;
    }

    public String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return fileName.substring(dotIndex + 1);
    }
}

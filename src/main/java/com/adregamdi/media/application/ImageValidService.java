package com.adregamdi.media.application;

import com.adregamdi.media.domain.ImageTarget;
import com.adregamdi.media.exception.ImageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class ImageValidService {

    private final static int TRAVELOGUE_IMAGE_MAX_LENGTH = 20;
    private final static int PLACE_REVIEW_IMAGE_MAX_LENGTH = 3;

    public String checkImageFile(String fileName) {
        if (isFileNameInvalid(fileName)) {
            throw new ImageException.InvalidFileNameException(fileName);
        }

        String extension = getFileExtension(fileName);

        if (isImageExtension(extension)) {
            return extension;
        } else {
            throw new ImageException.UnSupportedImageTypeException(fileName);
        }
    }

    public void checkMaxLength(List<MultipartFile> imageFiles, ImageTarget imageTarget) {

        int requestListLength = imageFiles.size();
        int maxLength = getMaxLengthByImageTarget(imageTarget);

        if (maxLength < requestListLength) {
            throw new ImageException.InvalidImageLengthException("이미지 업로드 개수는 " + maxLength + "개 이하입니다.");
        }

    }

    private int getMaxLengthByImageTarget(ImageTarget imageTarget) {
        return switch (imageTarget) {
            case TRAVELOGUE -> TRAVELOGUE_IMAGE_MAX_LENGTH;
            case PLACEREVIEW -> PLACE_REVIEW_IMAGE_MAX_LENGTH;
            default -> 1;
        };
    }

    public String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return fileName.substring(dotIndex + 1);
    }

    private boolean isFileNameInvalid(String fileName) {
        return fileName == null || fileName.isBlank() || !fileName.contains(".");
    }

    private boolean isImageExtension(String extension) {

        String[] imageExtensions = {"jpg", "jpeg", "png", "gif", "bmp", "heic", "heif"};

        for (String ext : imageExtensions) {
            if (extension.equalsIgnoreCase(ext)) {
                return true;
            }
        }

        return false;
    }

    public String getFileNameFromUrl(String url) {
        if (url == null || url.lastIndexOf('.') == -1) {
            return null; // 확장자를 찾을 수 없으면 null 반환
        }

        return url.substring(url.lastIndexOf('/') + 1);
    }

    public boolean isSameImage(String image1, String image2) {
        return Objects.equals(image1, image2);
    }

    public boolean isHeifOrHeic(String fileExtension) {
        return "heif".equalsIgnoreCase(fileExtension) || "heic".equalsIgnoreCase(fileExtension);
    }
}

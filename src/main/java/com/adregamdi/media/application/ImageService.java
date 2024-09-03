package com.adregamdi.media.application;

import com.adregamdi.media.domain.ImageTarget;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    String uploadImage(MultipartFile image, String memberId, ImageTarget imageTarget) throws IOException;

    List<String> uploadImages(List<MultipartFile> imageList, String memberId, ImageTarget imageTarget) throws IOException;

    void saveImage(String imageUrl, ImageTarget imageTarget);

    void saveImages(List<String> imageUrlList, ImageTarget imageTarget);

    void saveTargetNo(List<String> imageUrlList, ImageTarget imageTarget, Long targetNo);

    void saveTargetNo(String imageUrl, ImageTarget imageTarget, Long targetNo);

    void updateImage(String newImageUrl, ImageTarget imageTarget, Long targetNo);

    void deleteImageList(List<String> imageUrlList);

    void deleteImageFromStorage(String imageUrl);

    void deleteImageFromDB(String imageUrl);

    void deleteUnassignedImages();

    byte[] resizeImage(MultipartFile multipartFile) throws IOException;

}

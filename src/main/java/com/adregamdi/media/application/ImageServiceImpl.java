package com.adregamdi.media.application;

import com.adregamdi.media.domain.Image;
import com.adregamdi.media.domain.ImageTarget;
import com.adregamdi.media.exception.ImageException;
import com.adregamdi.media.infrastructure.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final FileUploadService fileUploadService;
    private final ImageValidService imageValidService;
    private final ImageRepository imageRepository;

    private final static int IMAGE_RESIZE_TARGET_WIDTH = 650;

    private Image getImageByImageUrl(String imageUrl) {
        Image image = imageRepository.findImageByImageUrl(imageUrl)
                .orElseThrow(() -> new ImageException.ImageNotFoundException(imageUrl));
        log.info("성공적으로 이미지를 조회하였습니다. imageUrl: {}", imageUrl);
        return image;
    }

    private Image getImageByImageTargetAndTargetNo(ImageTarget imageTarget, Long targetNo) {
        Image image = imageRepository.findImageByImageTargetAndTargetNo(imageTarget, targetNo)
                .orElseThrow(() -> new ImageException.ImageNotFoundException(targetNo));
        log.info("성공적으로 이미지를 조회하였습니다. targetNo: {}", targetNo);
        return image;
    }

    @Override
    public String uploadImage(MultipartFile image, String memberId, ImageTarget imageTarget) throws IOException {

        imageValidService.checkImageFile(image.getOriginalFilename());

        String dirName = imageTarget.toString() + "/" + memberId;
        String key = fileUploadService.buildKey(image, dirName);
        byte[] resizedImage = resizeImage(image);

        return fileUploadService.uploadFile(resizedImage, key);
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> imageList, String memberId, ImageTarget imageTarget) throws IOException {

        imageValidService.checkMaxLength(imageList, imageTarget);

        List<String> uploadedUrls = new ArrayList<>();
        for (MultipartFile image : imageList) {
            String url = uploadImage(image, memberId, imageTarget);
            uploadedUrls.add(url);
        }

        return uploadedUrls;
    }

    @Override
    @Transactional
    public void saveImage(String imageUrl, ImageTarget imageTarget) {

        Image image = Image.builder()
                .imageUrl(imageUrl)
                .imageTarget(imageTarget)
                .build();

        imageRepository.save(image);
    }

    @Override
    @Transactional
    public void saveImages(List<String> imageUrlList, ImageTarget imageTarget) {
        List<Image> imageList = new ArrayList<>();

        for (String imageUrl : imageUrlList) {
            imageList.add(
                    Image.builder()
                            .imageUrl(imageUrl)
                            .imageTarget(imageTarget)
                            .build()
            );
        }

        imageRepository.saveAll(imageList);
        log.info("이미지 리스트가 DB에 추가되었습니다.");
    }

    @Override
    public void saveTargetNo(List<String> imageUrlList, ImageTarget imageTarget, Long targetNo) {

        for (String imageUrl : imageUrlList) {
            saveTargetNo(imageUrl, imageTarget, targetNo);
        }
    }

    @Override
    public void saveTargetNo(String imageUrl, ImageTarget imageTarget, Long targetNo) {

        log.info("이미지에 아이디를 할당합니다. imageTarget: {}, targetNo: {}", imageTarget, targetNo);
        String filename = imageValidService.getFileNameFromUrl(imageUrl);
        imageValidService.checkImageFile(filename);

        Image image = getImageByImageUrl(imageUrl);
        image.updateImageTarget(imageTarget);
        image.updateTargetNo(targetNo);
        log.info("{} 번의 이미지가 {} 의 {} 번의 엔테티로 할당되었습니다,", image.getImageNo(), image.getImageTarget(), image.getTargetNo());
    }

    @Override
    @Transactional
    public void updateImage(String newImageUrl, ImageTarget target, Long targetNo) {
        Image existingImage = imageRepository.findImageByTargetNoAndImageTarget(targetNo, target)
                .orElse(null);

        // 새 이미지 URL이 제공되었고, 기존 이미지와 다른 경우
        if (!newImageUrl.isBlank() && (existingImage == null || !imageValidService.isSameImage(existingImage.getImageUrl(), newImageUrl))) {
            handleNewImage(existingImage, target, targetNo, newImageUrl);
        }
        // 새 이미지 URL이 비어있는 경우 (이미지 삭제)
        else if (newImageUrl.isBlank() && existingImage != null) {
            handleImageDeletion(existingImage);
        }
        // 변경사항이 없는 경우
        else {
            log.info("이미지 변경 사항이 없습니다. target: {}, targetNo: {}", target, targetNo);
        }
    }

    private void handleNewImage(Image existingImage, ImageTarget target, Long targetNo, String newImageUrl) {
        // 새 이미지 URL에 대한 유효성 검사
        imageValidService.checkImageFile(imageValidService.getFileNameFromUrl(newImageUrl));

        // 기존 이미지가 있다면 스토리지에서 삭제
        if (existingImage != null && !existingImage.getImageUrl().isBlank()) {
            deleteImageFromStorage(existingImage.getImageUrl());
            imageRepository.delete(existingImage);
        }

        // 새 이미지에 대한 targetNo와 imageTarget 할당
        saveTargetNo(newImageUrl, target, targetNo);

        log.info("{} - {}의 이미지가 {}로 변경되었습니다.", target, targetNo, newImageUrl);
    }

    private void handleImageDeletion(Image existingImage) {
        deleteImageFromStorage(existingImage.getImageUrl());
        imageRepository.delete(existingImage);
        log.info("{} 이미지가 삭제되었습니다.", existingImage.getImageUrl());
    }

    @Override
    @Transactional
    public void deleteImageList(List<String> imageUrlList) {

        List<Image> existingImages = imageRepository.findByImageUrlIn(imageUrlList);

        List<String> existingUrls = existingImages.stream()
                .map(Image::getImageUrl)
                .toList();

        // S3에서 이미지 삭제
        for (String imageUrl : existingUrls) {
            fileUploadService.deleteFile(imageUrl);
        }

        // DB에서 이미지 정보 일괄 삭제
        int deletedCount = imageRepository.deleteAllByImageUrlIn(existingUrls);

        log.info("DB와 Storage 에서 {}개의 이미지가 삭제되었습니다.", deletedCount);
    }

    @Override
    public void deleteImageFromStorage(String imageUrl) {

        fileUploadService.deleteFile(imageUrl);
        log.info("{} 이미지가 Storage 에서 이미지가 삭제되었습니다.", imageUrl);
    }

    @Override
    @Transactional
    public void deleteImageFromDB(String imageUrl) {
        Image image = getImageByImageUrl(imageUrl);
        imageRepository.delete(image);
        log.info("{} 이미지가 DB에서 삭제되었습니다.", imageUrl);
    }

    @Override
    public byte[] resizeImage(MultipartFile multipartFile) throws IOException {

        log.info("{} 이미지를 리사이징합니다.", multipartFile.getOriginalFilename());
        String fileExtension = imageValidService.getFileExtension(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        log.info("fileExtension: {}", fileExtension);

        // HEIC/HEIF 파일은 리사이징하지 않고 그대로 반환
        if (imageValidService.isHeifOrHeic(fileExtension)) {
            return multipartFile.getBytes();
        }

        BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());
        if (originalImage == null) {
            throw new IOException("이미지 리사이징 중 오류가 발생하였습니다.");
        }

        BufferedImage resizedImage = Scalr.resize(
                originalImage,
                Scalr.Method.QUALITY,
                Scalr.Mode.FIT_TO_WIDTH,
                IMAGE_RESIZE_TARGET_WIDTH,
                Scalr.THRESHOLD_QUALITY_BALANCED
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean success = ImageIO.write(resizedImage, fileExtension, outputStream);

        if (!success) {
            throw new IOException("이미지를 " + fileExtension + " 형식으로 저장할 수 없습니다.");
        }

        log.info("성공적으로 이미지가 리사이징되었습니다.");
        return outputStream.toByteArray();
    }

    @Override
    @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 실행
    public void deleteUnassignedImages() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7); // 일주일 동안 할당되지 않은 이미지 삭제
        log.info("{}일 이후의 할당되지 않은 사진들을 삭제합니다.", cutoffDate);
        List<String> unassignedImageUrlList = imageRepository.findUnassignedImagesBeforeDate(cutoffDate);

        // S3에서 이미지 삭제
        for (String url : unassignedImageUrlList) {
            fileUploadService.deleteFile(url);
        }

        int deletedCount = imageRepository.deleteAllByImageUrlIn(unassignedImageUrlList);
        log.info("DB와 Storage 에서 {}개의 이미지가 삭제되었습니다.", deletedCount);
    }
}
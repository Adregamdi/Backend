package com.adregamdi.media.application;

import com.adregamdi.media.domain.Image;
import com.adregamdi.media.domain.ImageTarget;
import com.adregamdi.media.exception.ImageException;
import com.adregamdi.media.infrastructure.ImageRepository;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
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

    private final static int IMAGE_RESIZE_TARGET_WIDTH = 650;
    private final FileUploadService fileUploadService;
    private final ImageValidService imageValidService;
    private final ImageRepository imageRepository;

    private Image getImageByImageUrl(String imageUrl) {
        Image image = imageRepository.findImageByImageUrl(imageUrl)
                .orElseThrow(() -> new ImageException.ImageNotFoundException(imageUrl));
        log.info("성공적으로 이미지를 조회하였습니다. imageUrl: {}", imageUrl);
        return image;
    }

    private Image getImageByImageTargetAndTargetNo(ImageTarget imageTarget, String targetId) {
        Image image = imageRepository.findImageByImageTargetAndTargetId(imageTarget, targetId)
                .orElseThrow(() -> new ImageException.ImageNotFoundException(targetId));
        log.info("성공적으로 이미지를 조회하였습니다. targetNo: {}", targetId);
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
    public void saveTargetId(List<String> imageUrlList, ImageTarget imageTarget, String targetId) {

        for (String imageUrl : imageUrlList) {
            this.saveTargetId(imageUrl, imageTarget, targetId);
        }
    }

    @Override
    public void saveTargetId(String imageUrl, ImageTarget imageTarget, String targetId) {

        log.info("이미지에 아이디를 할당합니다. imageTarget: {}, targetNo: {}", imageTarget, targetId);
        String filename = imageValidService.getFileNameFromUrl(imageUrl);
        imageValidService.checkImageFile(filename);

//        Image image = getImageByImageUrl(imageUrl);
//        image.updateImageTarget(imageTarget);
//        image.updateTargetId(targetId);
        Image image = imageRepository.save(Image.builder()
                .imageUrl(imageUrl)
                .imageTarget(imageTarget)
                .targetId(targetId)
                .build());
        log.info("{} 번의 이미지가 {} 의 {} 번의 엔테티로 할당되었습니다,", image.getImageNo(), image.getImageTarget(), image.getTargetId());
    }

    @Override
    @Transactional
    public void updateImages(List<String> imageUrlList, ImageTarget imageTarget, String targetId) {
        imageUrlList.forEach(url -> updateImage(url, imageTarget, targetId));
    }

    @Override
    @Transactional
    public void updateImage(String newImageUrl, ImageTarget target, String targetId) {
        Image existingImage = imageRepository.findImageByTargetIdAndImageTarget(targetId, target)
                .orElse(null);

        // 새 이미지 URL이 제공되었고, 기존 이미지와 다른 경우
        if (!newImageUrl.isBlank() && (existingImage == null || !imageValidService.isSameImage(existingImage.getImageUrl(), newImageUrl))) {
            handleNewImage(existingImage, target, targetId, newImageUrl);
        }
        // 새 이미지 URL이 비어있는 경우 (이미지 삭제)
        else if (newImageUrl.isBlank() && existingImage != null) {
            handleImageDeletion(existingImage);
        }
        // 변경사항이 없는 경우
        else {
            log.info("이미지 변경 사항이 없습니다. target: {}, targetNo: {}", target, targetId);
        }
    }

    private void handleNewImage(Image existingImage, ImageTarget target, String targetId, String newImageUrl) {
        // 새 이미지 URL에 대한 유효성 검사
        imageValidService.checkImageFile(imageValidService.getFileNameFromUrl(newImageUrl));

        // 기존 이미지가 있다면 스토리지에서 삭제
        if (existingImage != null && !existingImage.getImageUrl().isBlank()) {
            deleteImageFromStorage(existingImage.getImageUrl());
            imageRepository.delete(existingImage);
        }

        // 새 이미지에 대한 targetNo와 imageTarget 할당
        saveTargetId(newImageUrl, target, targetId);

        log.info("{} - {}의 이미지가 {}로 변경되었습니다.", target, targetId, newImageUrl);
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

        BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());
        if (originalImage == null) {
            throw new IOException("이미지 리사이징 중 오류가 발생하였습니다.");
        }

        originalImage = rotateImageIfRequired(originalImage, multipartFile);

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


    private BufferedImage rotateImageIfRequired(BufferedImage image, MultipartFile multipartFile) throws IOException {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(multipartFile.getInputStream());
            Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                int orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
                switch (orientation) {
                    case 3:
                        return Scalr.rotate(image, Scalr.Rotation.CW_180);
                    case 6:
                        return Scalr.rotate(image, Scalr.Rotation.CW_90);
                    case 8:
                        return Scalr.rotate(image, Scalr.Rotation.CW_270);
                }
            }
        } catch (ImageProcessingException | com.drew.metadata.MetadataException e) {
            log.warn("이미지 방향 정보 읽기 실패", e);
        }
        return image;
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
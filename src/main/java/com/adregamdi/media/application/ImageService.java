package com.adregamdi.media.application;

import lombok.RequiredArgsConstructor;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final FileUploadService fileUploadService;

    private final static int IMAGE_RESIZE_TARGET_WIDTH = 650;

    public String getEncodedFileName(String key) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }

    public byte[] resizeImage(MultipartFile multipartFile) throws IOException {
        BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());
        BufferedImage resizedImage =
                Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, IMAGE_RESIZE_TARGET_WIDTH, Scalr.THRESHOLD_QUALITY_BALANCED);
        String fileExtension = fileUploadService.extractFileExtension(Objects.requireNonNull(multipartFile));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, fileExtension, outputStream);

        return outputStream.toByteArray();
    }

}

package com.adregamdi.media.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.media.application.ImageService;
import com.adregamdi.media.domain.ImageTarget;
import com.adregamdi.media.dto.response.CreateImageListResponse;
import com.adregamdi.media.dto.response.CreateImageResponse;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<CreateImageResponse>> createImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("image") MultipartFile image,
            @RequestParam("image_target") @Pattern(regexp = "(?i)^(PROFILE|PLACEREVIEW|TRAVELOGUE)$",
                    message = "유효한 ImageTarget이 아닙니다 (PROFILE, PLACEREVIEW, TRAVELOGUE)") String imageTargetStr
            ) throws IOException {


        String memberId = userDetails.getUsername();
        ImageTarget imageTarget = ImageTarget.valueOf(imageTargetStr.toUpperCase());
        String imageUrl = imageService.uploadImage(image, memberId, imageTarget);
        imageService.saveImage(imageUrl, imageTarget);

        return ResponseEntity.ok()
                .body(ApiResponse.<CreateImageResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(new CreateImageResponse(imageUrl))
                        .build());
    }

    @PostMapping("/list")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<CreateImageListResponse>> createImages(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("images") List<MultipartFile> imgList,
            @RequestParam("image_target") @Pattern(regexp = "(?i)^(PROFILE|PLACEREVIEW|TRAVELOGUE)$",
                    message = "유효한 ImageTarget이 아닙니다 (PROFILE, PLACEREVIEW, TRAVELOGUE)") String imageTargetStr
    ) throws IOException {
        String memberId = userDetails.getUsername();
        ImageTarget imageTarget = ImageTarget.valueOf(imageTargetStr.toUpperCase());
        List<String> imageUrlList = imageService.uploadImages(imgList, memberId, imageTarget);
        imageService.saveImages(imageUrlList, imageTarget);

        return ResponseEntity.ok()
                .body(ApiResponse.<CreateImageListResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(new CreateImageListResponse(imageUrlList) )
                        .build());
    }

    @DeleteMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> deleteImages(
            @RequestPart("images") List<String> imageUrlList
    ) {

        imageService.deleteImageList(imageUrlList);

        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
}

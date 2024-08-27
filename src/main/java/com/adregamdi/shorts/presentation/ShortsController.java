package com.adregamdi.shorts.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.shorts.application.ShortsService;
import com.adregamdi.shorts.application.VideoService;
import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.request.UpdateShortsRequest;
import com.adregamdi.shorts.dto.response.GetShortsResponse;
import com.adregamdi.shorts.dto.response.SaveVideoResponse;
import com.adregamdi.shorts.dto.response.UploadVideoDTO;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ws.schild.jave.EncoderException;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shorts")
public class ShortsController {

    private final ShortsService shortsService;
    private final VideoService videoService;

    private static String MEMBER_ID_FOR_TEST = "memberId";


    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @GetMapping("/list")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetShortsResponse>> getShorts(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(value = "shorts_id", required = false, defaultValue = "0") @PositiveOrZero final Long lastShortsId,
            @RequestParam(value = "size", defaultValue = "10") @Positive final int size
    ) {

//        String memberId = userDetails.getUsername();
        GetShortsResponse response = shortsService.getShorts(MEMBER_ID_FOR_TEST, lastShortsId, size);

        return ResponseEntity
                .ok()
                .body(ApiResponse.<GetShortsResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(response)
                        .build());
    }

    @GetMapping("/user-list")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetShortsResponse>> getUserShorts(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(value = "shorts_id", required = false, defaultValue = "0") @PositiveOrZero final Long lastShortsId,
            @RequestParam(value = "size", defaultValue = "10") @Positive final int size
    ) {
        //        String memberId = userDetails.getUsername();
        GetShortsResponse response = shortsService.getUserShorts(MEMBER_ID_FOR_TEST, lastShortsId, size);
        return ResponseEntity
                .ok()
                .body(ApiResponse.<GetShortsResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(response)
                        .build());
    }

    @GetMapping("/stream/{shorts_id}")
    @MemberAuthorize
    public ResponseEntity<StreamingResponseBody> streamShort(@PathVariable(value = "shorts_id") Long shortsId) {

        String s3Key = shortsService.getS3KeyByShortId(shortsId);
        log.info("{} 스트리밍을 시작합니다.", s3Key);
        S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucketName, s3Key));
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        StreamingResponseBody responseBody = outputStream -> {
            byte[] buffer = new byte[1024];
            int bytesRead;
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush();
                }
            } finally {
                // 스트림을 안전하게 닫기 위해 try-finally 사용
                inputStream.close();  // InputStream은 명시적으로 닫아줍니다.
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(responseBody);
    }


    @PostMapping("/upload-video")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<SaveVideoResponse>> uploadVideo(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("shorts") final MultipartFile videoFile
    ) throws EncoderException {

//        String memberId = userDetails.getUsername();
        UploadVideoDTO videoUrlDTO = videoService.uploadVideo(videoFile, MEMBER_ID_FOR_TEST);
        SaveVideoResponse response = shortsService.saveVideo(videoUrlDTO, MEMBER_ID_FOR_TEST);

        return ResponseEntity.ok()
                .body(ApiResponse.<SaveVideoResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(response)
                        .build());
    }

    @PostMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> createShorts(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateShortsRequest request
    ) {

//        shortsService.saveShorts(userDetails.getUsername(), request);
        shortsService.saveShorts(MEMBER_ID_FOR_TEST, request);
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PutMapping("/update")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> updateShorts(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateShortsRequest request
    ) {

        shortsService.updateShorts(MEMBER_ID_FOR_TEST, request);

        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @DeleteMapping("/{shorts_id}")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> deleteShort(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable(value = "shorts_id") @NotEmpty(message = "쇼츠 값이 필요합니다.") Long shortsId
            ) {

        String memberId = userDetails.getUsername();
        shortsService.deleteShorts(MEMBER_ID_FOR_TEST, shortsId);
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

}

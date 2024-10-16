package com.adregamdi.shorts.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.shorts.application.ShortsService;
import com.adregamdi.shorts.application.VideoService;
import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.request.GetShortsByPlaceIdRequest;
import com.adregamdi.shorts.dto.request.UpdateShortsRequest;
import com.adregamdi.shorts.dto.response.*;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ws.schild.jave.EncoderException;

import java.io.IOException;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shorts")
public class ShortsController {

    private final ShortsService shortsService;
    private final VideoService videoService;

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @GetMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetShortsByShortsIdResponse>> getShortsByShortsId(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(value = "shorts_id") @Positive final Long shortsId
    ) {
        GetShortsByShortsIdResponse response = shortsService.getShortsByShortsId(userDetails.getUsername(), shortsId);
        return ResponseEntity.ok()
                .body(ApiResponse.<GetShortsByShortsIdResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(response)
                        .build());
    }

    @GetMapping("/list")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetShortsResponse>> getShorts(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(value = "shorts_id", required = false) @PositiveOrZero final Long lastShortsId,
            @RequestParam(value = "size", defaultValue = "10") @Positive final int size
    ) {


        GetShortsResponse response = shortsService.getShorts(userDetails.getUsername(), lastShortsId != null ? lastShortsId : Long.MAX_VALUE, size);

        return ResponseEntity
                .ok()
                .body(ApiResponse.<GetShortsResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(response)
                        .build());
    }

    @GetMapping("/my-list")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetShortsResponse>> getMyShorts(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(value = "shorts_id", required = false) @PositiveOrZero final Long lastShortsId,
            @RequestParam(value = "size", defaultValue = "10") @Positive final int size
    ) {
        GetShortsResponse response = shortsService.getUserShorts(userDetails.getUsername(), lastShortsId != null ? lastShortsId : Long.MAX_VALUE, size);
        return ResponseEntity
                .ok()
                .body(ApiResponse.<GetShortsResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(response)
                        .build());
    }

    @GetMapping("/list/member")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetShortsResponse>> getMemberShorts(
            @RequestParam(value = "member_id") final String memberId,
            @RequestParam(value = "shorts_id", required = false)
            @PositiveOrZero final Long lastShortsId,
            @RequestParam(value = "size", required = false, defaultValue = "10")
            @Positive final int size
    ) {
        GetShortsResponse response = shortsService.getUserShorts(
                memberId,
                lastShortsId != null ? lastShortsId : Long.MAX_VALUE,
                size
        );
        return ResponseEntity.ok()
                .body(ApiResponse.<GetShortsResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(response)
                        .build());
    }

    @GetMapping("/list/hot")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetShortsResponse>> getHotShorts(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(value = "like_count", required = false) @PositiveOrZero final Integer lastLikeCount,
            @RequestParam(value = "size", defaultValue = "10") @Positive final int size
    ) {
        GetShortsResponse response = shortsService.getHotShorts(userDetails.getUsername(), lastLikeCount != null ? lastLikeCount : Integer.MAX_VALUE, size);
        return ResponseEntity.ok()
                .body(ApiResponse.<GetShortsResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(response)
                        .build());
    }

    @GetMapping("/place")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetShortsByPlaceIdResponse>> getShortsByPlaceId(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam("place_id") @Positive Long placeId,
            @RequestParam(value = "shorts_id", required = false) @PositiveOrZero Long lastShortsId,
            @RequestParam(value = "size", defaultValue = "10") @Positive int size
    ) {

        GetShortsByPlaceIdRequest request = new GetShortsByPlaceIdRequest(placeId, lastShortsId != null ? lastShortsId : Long.MAX_VALUE, size);
        GetShortsByPlaceIdResponse response = shortsService.getShortsByPlaceId(userDetails.getUsername(), request);

        return ResponseEntity.ok()
                .body(ApiResponse.<GetShortsByPlaceIdResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(response)
                        .build());
    }

    @PostMapping("/upload-video")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<SaveVideoResponse>> uploadVideo(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("shorts") MultipartFile videoFile
    ) throws EncoderException {

        UploadVideoDTO videoUrlDTO = videoService.uploadVideo(videoFile, userDetails.getUsername());
        SaveVideoResponse response = shortsService.saveVideo(videoUrlDTO, userDetails.getUsername());

        return ResponseEntity.ok()
                .body(ApiResponse.<SaveVideoResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(response)
                        .build());
    }

    @GetMapping("/stream/{shorts_id}")
    @MemberAuthorize
    public ResponseEntity<StreamingResponseBody> streamShort(@PathVariable(value = "shorts_id") Long shortsId) {
        String s3Key = shortsService.getS3KeyByShortId(shortsId);
        log.info("{} 스트리밍을 시작합니다.", s3Key);

        StreamingResponseBody responseBody = outputStream -> {
            long chunkSize = 1024 * 1024; // 1MB 청크 단위
            long startByte = 0;

            S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucketName, s3Key));
            ObjectMetadata metadata = s3Object.getObjectMetadata();
            long contentLength = metadata.getContentLength();
            log.info("총 파일 크기: {} bytes", contentLength);

            // Ranged GET 요청을 사용하여 청크 단위로 파일을 읽어옴
            while (startByte < contentLength) {
                long endByte = Math.min(startByte + chunkSize - 1, contentLength - 1);
                GetObjectRequest rangeRequest = new GetObjectRequest(bucketName, s3Key)
                        .withRange(startByte, endByte);

                try (S3Object rangeObject = amazonS3Client.getObject(rangeRequest);
                     S3ObjectInputStream inputStream = rangeObject.getObjectContent()) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        outputStream.flush();

                        if (Thread.currentThread().isInterrupted()) {
                            log.warn("클라이언트 연결이 종료되었습니다.");
                            inputStream.abort();
                            return;
                        }
                    }

                    startByte = endByte + 1;
                    log.info("스트리밍 진행: {}%", (startByte * 100) / contentLength);

                } catch (IOException e) {
                    log.error("청크 스트리밍 중 오류 발생", e);
                    throw e; // 상위 try-catch로 예외를 전파
                }
            }
        };

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, "video/mp4");
        responseHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(responseBody);
    }

    @PostMapping("")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> createShorts(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateShortsRequest request
    ) {
        shortsService.saveShorts(userDetails.getUsername(), request);

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

        shortsService.updateShorts(userDetails.getUsername(), request);

        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @DeleteMapping("/delete/{shorts_id}")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> deleteShort(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable(value = "shorts_id") Long shortsId
    ) {
        shortsService.deleteShorts(userDetails.getUsername(), shortsId);

        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

}

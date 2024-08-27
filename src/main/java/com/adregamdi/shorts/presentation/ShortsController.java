package com.adregamdi.shorts.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.core.jwt.service.JwtService;
import com.adregamdi.shorts.application.ShortsService;
import com.adregamdi.shorts.application.VideoService;
import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.request.UpdateShortsRequest;
import com.adregamdi.shorts.dto.response.GetShortsResponse;
import com.adregamdi.shorts.dto.response.SaveVideoResponse;
import com.adregamdi.shorts.dto.response.UploadVideoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shorts")
public class ShortsController {

    private final JwtService jwtService;
    private final ShortsService shortsService;
    private final VideoService videoService;

    private static String MEMBER_ID_FOR_TEST = "memberId";

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

    @PostMapping("/upload-video")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<SaveVideoResponse>> uploadVideo(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("shorts") final MultipartFile videoFile
    ) throws EncoderException {

        String memberId = userDetails.getUsername();
        UploadVideoDTO videoUrlDTO = videoService.uploadVideo(videoFile, memberId);
        SaveVideoResponse response = shortsService.saveVideo(videoUrlDTO, memberId);

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

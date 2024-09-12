package com.adregamdi.like.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.like.application.LikesService;
import com.adregamdi.like.dto.request.CreateLikesRequest;
import com.adregamdi.like.dto.request.GetLikesContentsRequest;
import com.adregamdi.like.dto.response.CreateShortsLikeResponse;
import com.adregamdi.like.dto.response.GetLikesContentsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikesController {

    private final LikesService likesService;

    @GetMapping("/content-list")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetLikesContentsResponse<?>>> getLikesContents(
            @Valid @ModelAttribute GetLikesContentsRequest request
    ) {
        log.info("last like id: {}", request.lastLikeId());
        log.info("size: {}", request.size());
        GetLikesContentsResponse<?> response = likesService.getLikesContents(request);
        return ResponseEntity.ok()
                .body(ApiResponse.<GetLikesContentsResponse<?>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(null)
                        .build());
    }

    @PostMapping()
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateLikesRequest request
    ) {
        likesService.create(userDetails.getUsername(), request);
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PostMapping("/shorts/{shorts_id}")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<CreateShortsLikeResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable(value = "shorts_id") Long shorts_id
    ) {

        return ResponseEntity.ok()
                .body(ApiResponse.<CreateShortsLikeResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(likesService.createShortsLike(userDetails.getUsername(), shorts_id))
                        .build());
    }

    @DeleteMapping("/{likeId}")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("likeId") Long likeId
    ) {
        String memberId = userDetails.getUsername();
        likesService.delete(memberId, likeId);
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
}
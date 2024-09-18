package com.adregamdi.like.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.like.application.LikesService;
import com.adregamdi.like.dto.request.CreateLikesRequest;
import com.adregamdi.like.dto.request.DeleteLikeRequest;
import com.adregamdi.like.dto.request.GetLikesContentsRequest;
import com.adregamdi.like.dto.response.CreateShortsLikeResponse;
import com.adregamdi.like.dto.response.GetLikesContentsResponse;
import com.adregamdi.member.domain.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikesController {

    private final LikesService likesService;

    @GetMapping("/content-list/all")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetLikesContentsResponse<?>>> getLikesContents(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "like_id", defaultValue = "0") Long lastLikeId,
            @RequestParam(value = "size") @Positive int size
    ) {
        GetLikesContentsRequest request = new GetLikesContentsRequest(userDetails.getUsername(), lastLikeId, size);
        return ResponseEntity.ok()
                .body(ApiResponse.<GetLikesContentsResponse<?>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(likesService.getLikesContents(request))
                        .build());
    }

    @GetMapping("/content-list/travelogue")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetLikesContentsResponse<?>>> getLikesContentsOfTravelogue(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "like_id", defaultValue = "0") Long lastLikeId,
            @RequestParam(value = "size") @Positive int size
    ) {
        GetLikesContentsRequest request = new GetLikesContentsRequest(userDetails.getUsername(), lastLikeId, size);
        return ResponseEntity.ok()
                .body(ApiResponse.<GetLikesContentsResponse<?>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(likesService.getLikesContentsOfTravelogue(request))
                        .build());
    }

    @GetMapping("/content-list/place")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetLikesContentsResponse<?>>> getLikesContentsOfPlace(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "like_id", defaultValue = "0") Long lastLikeId,
            @RequestParam(value = "size") @Positive int size
    ) {
        GetLikesContentsRequest request = new GetLikesContentsRequest(userDetails.getUsername(), lastLikeId, size);
        return ResponseEntity.ok()
                .body(ApiResponse.<GetLikesContentsResponse<?>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(likesService.getLikesContentsOfPlace(request))
                        .build());
    }

    @GetMapping("/content-list/shorts")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetLikesContentsResponse<?>>> getLikesContentsOfShorts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "like_id", defaultValue = "0") Long lastLikeId,
            @RequestParam(value = "size") @Positive int size
    ) {
        GetLikesContentsRequest request = new GetLikesContentsRequest(userDetails.getUsername(), lastLikeId, size);
        return ResponseEntity.ok()
                .body(ApiResponse.<GetLikesContentsResponse<?>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(likesService.getLikesContentsOfShorts(request))
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

    @DeleteMapping()
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute DeleteLikeRequest request
    ) {
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        String roleName = role.startsWith("ROLE_") ? role.substring(5) : role;

        Role memberRole = Role.valueOf(roleName);

        likesService.delete(userDetails.getUsername(), memberRole, request);
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }
}
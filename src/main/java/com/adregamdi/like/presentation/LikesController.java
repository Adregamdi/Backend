package com.adregamdi.like.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.like.application.LikesService;
import com.adregamdi.like.domain.enumtype.SelectedType;
import com.adregamdi.like.dto.request.CreateLikesRequest;
import com.adregamdi.like.dto.request.DeleteLikeRequest;
import com.adregamdi.like.dto.request.GetLikesContentsRequest;
import com.adregamdi.like.dto.response.CreateLikesResponse;
import com.adregamdi.like.dto.response.CreateShortsLikeResponse;
import com.adregamdi.like.dto.response.GetLikesContentsResponse;
import com.adregamdi.member.domain.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
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

    @GetMapping("/content-list")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetLikesContentsResponse<?>>> getLikesContents(
            @AuthenticationPrincipal UserDetails userDetails,
            @Pattern(regexp = "(?i)ALL|PLACE|TRAVELOGUE|SHORTS", message = "전체(ALL) 혹은 쇼츠(SHORTS), 장소(PLACE), 여행기(TRAVELOGUE)만 입력 가능합니다.")
            @RequestParam(value = "select-content") String selectedContent,
            @RequestParam(value = "like_id", required = false) Long lastLikeId,
            @RequestParam(value = "size") @Positive int size
    ) {
        GetLikesContentsRequest request = new GetLikesContentsRequest(
                SelectedType.valueOf(selectedContent.toUpperCase()),
                userDetails.getUsername(),
                lastLikeId != null ? lastLikeId : Long.MAX_VALUE,
                size);
        return ResponseEntity.ok()
                .body(ApiResponse.<GetLikesContentsResponse<?>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(likesService.getLikesContents(request))
                        .build());
    }

    @PostMapping()
    @MemberAuthorize
    public ResponseEntity<ApiResponse<CreateLikesResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateLikesRequest request
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<CreateLikesResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(likesService.create(userDetails.getUsername(), request))
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
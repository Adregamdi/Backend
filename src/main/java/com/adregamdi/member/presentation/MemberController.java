package com.adregamdi.member.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;

import com.adregamdi.member.application.MemberService;
import com.adregamdi.member.domain.SelectedType;
import com.adregamdi.member.dto.request.GetMemberContentsRequest;
import com.adregamdi.member.dto.request.UpdateMyMemberRequest;
import com.adregamdi.member.dto.response.GetMemberContentsResponse;
import com.adregamdi.member.dto.response.GetMyMemberResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/member")
@RestController
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/me")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetMyMemberResponse>> getMyMember(@AuthenticationPrincipal final UserDetails userDetails) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetMyMemberResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(memberService.getMyMember(userDetails.getUsername()))
                        .build());
    }

    @PatchMapping("/me")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> update(
            @RequestBody @Valid final UpdateMyMemberRequest request,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        memberService.update(request, userDetails.getUsername());
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @PostMapping("/logout")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal final UserDetails userDetails) {
        memberService.logout(userDetails.getUsername());
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @DeleteMapping("/leave")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal final UserDetails userDetails) {
        memberService.delete(userDetails.getUsername());
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/contents")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetMemberContentsResponse<?>>> getMemberContents(
            @RequestParam(value = "member_id")
            String memberId,
            @Pattern(regexp = "(?i)ALL|TRAVELOGUE|SHORTS|PLACE_REVIEW", message = "ALL/Travelogue/Shorts/PLACE_REVIEW 만 입력 가능합니다.")
            @RequestParam(value = "select-content")
            String selectedContent,
            @RequestParam(value = "content_id", required = false)
            Long contentId,
            @RequestParam(value = "size", required = false, defaultValue = "10")
            int size
    ) {
        GetMemberContentsRequest request = new GetMemberContentsRequest(
                memberId,
                SelectedType.valueOf(selectedContent),
                contentId != null ? contentId : Long.MAX_VALUE,
                size);

        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(memberService.getMemberContents(request))
                        .build());
    }

}

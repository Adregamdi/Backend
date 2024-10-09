package com.adregamdi.member.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.core.jwt.service.JwtService;
import com.adregamdi.member.application.MemberService;
import com.adregamdi.member.dto.request.GetMemberContentsRequest;
import com.adregamdi.member.dto.request.UpdateMyMemberRequest;
import com.adregamdi.member.dto.response.GetMemberContentsResponse;
import com.adregamdi.member.dto.response.GetMyMemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/member")
@RestController
public class MemberController {
    private final JwtService jwtService;
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
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal final UserDetails userDetails,
            HttpServletRequest request
    ) {
        String accessToken = jwtService.extractAccessToken(request)
                .orElseThrow(() -> new IllegalArgumentException("액세스 토큰이 없습니다."));

        memberService.logout(userDetails.getUsername(), accessToken);

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

    @GetMapping("/contents/all")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetMemberContentsResponse<?>>> getMemberContentsOfAll(
            @RequestParam(value = "member_id") String memberId,
            @RequestParam(value = "create_at", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createAt,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        GetMemberContentsRequest request = new GetMemberContentsRequest(
                memberId,
                createAt != null ? createAt : LocalDateTime.now(),
                size
        );

        return ResponseEntity.ok()
                .body(ApiResponse.<GetMemberContentsResponse<?>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(memberService.getMemberContentsOfAll(request))
                        .build());
    }

}

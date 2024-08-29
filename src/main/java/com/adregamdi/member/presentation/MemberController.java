package com.adregamdi.member.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.member.application.MemberService;
import com.adregamdi.member.dto.request.UpdateMyMemberRequest;
import com.adregamdi.member.dto.response.GetMyMemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody final UpdateMyMemberRequest request,
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
}

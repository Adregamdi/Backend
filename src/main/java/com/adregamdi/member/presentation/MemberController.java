package com.adregamdi.member.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.member.application.MemberService;
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

    @GetMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login() {
        memberService.login();
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK)
                        .build());
    }

    @GetMapping("/me")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetMyMemberResponse>> getMyMember(@AuthenticationPrincipal final UserDetails userDetails) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetMyMemberResponse>builder()
                        .statusCode(HttpStatus.OK)
                        .data(memberService.getMyMember(userDetails.getUsername()))
                        .build());
    }

    @PostMapping("/logout")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal final UserDetails userDetails) {
        memberService.logout(userDetails.getUsername());
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK)
                        .build());
    }

    @DeleteMapping("/leave")
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal final UserDetails userDetails) {
        memberService.delete(userDetails.getUsername());
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.NO_CONTENT)
                        .build());
    }
}

package com.adregamdi.block.presentation;

import com.adregamdi.block.application.BlockService;
import com.adregamdi.block.dto.response.CreateBlockResponse;
import com.adregamdi.block.dto.response.GetMyBlockingMembers;
import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/block")
@RestController
public class BlockController {
    private final BlockService blockService;

    @PostMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<CreateBlockResponse>> create(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(name = "blocked_member_id") final String blockedMemberId
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<CreateBlockResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .data(blockService.create(userDetails.getUsername(), blockedMemberId))
                        .build()
                );
    }

    @GetMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetMyBlockingMembers>> getMyBlockingMembers(@AuthenticationPrincipal final UserDetails userDetails) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetMyBlockingMembers>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(blockService.getMyBlockingMembers(userDetails.getUsername()))
                        .build()
                );
    }

    @DeleteMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(name = "blocked_member_id") final String blockedMemberId
    ) {
        blockService.delete(userDetails.getUsername(), blockedMemberId);
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build()
                );
    }
}

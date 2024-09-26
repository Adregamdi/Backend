package com.adregamdi.block.presentation;

import com.adregamdi.block.application.BlockService;
import com.adregamdi.block.dto.request.CreateBlockRequest;
import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/block")
@RestController
public class BlockController {
    private final BlockService blockService;

    @PostMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> create(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestBody final CreateBlockRequest request
    ) {
        blockService.create(userDetails.getUsername(), request);
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
                );
    }
}

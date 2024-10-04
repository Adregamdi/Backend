package com.adregamdi.notification.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.notification.application.NotificationService;
import com.adregamdi.notification.dto.request.UpdateNotificationRequest;
import com.adregamdi.notification.dto.response.GetNotificationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/notification")
@RestController
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetNotificationResponse>> getMyNotification(
            @AuthenticationPrincipal final UserDetails userDetails,
            @RequestParam(defaultValue = "", required = false) final Long lastId
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetNotificationResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(notificationService.getMyNotification(userDetails.getUsername(), lastId))
                        .build()
                );
    }

    @PatchMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> update(@RequestBody @Valid final List<UpdateNotificationRequest> requests) {
        notificationService.update(requests);
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .build()
                );
    }
}

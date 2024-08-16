package com.adregamdi.schedule.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.schedule.application.ScheduleService;
import com.adregamdi.schedule.dto.request.CreateMyScheduleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/schedule")
@RestController
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> createMySchedule(
            @RequestBody final CreateMyScheduleRequest request,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        scheduleService.createMySchedule(request, userDetails.getUsername());
        return ResponseEntity.ok()
                .body(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
                );
    }
}

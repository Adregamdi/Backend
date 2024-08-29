package com.adregamdi.schedule.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.schedule.application.ScheduleService;
import com.adregamdi.schedule.dto.request.CreateMyScheduleRequest;
import com.adregamdi.schedule.dto.request.GetMyScheduleRequest;
import com.adregamdi.schedule.dto.response.GetMyScheduleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/schedule")
@RestController
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetMyScheduleResponse>> getMySchedule(
            @RequestBody @Valid List<GetMyScheduleRequest> requests,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetMyScheduleResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(scheduleService.getMySchedule(requests, userDetails.getUsername()))
                        .build()
                );
    }

    @PostMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<Void>> createMySchedule(
            @RequestBody @Valid final CreateMyScheduleRequest request,
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

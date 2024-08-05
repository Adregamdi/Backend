package com.adregamdi.place.presentation;

import com.adregamdi.core.annotation.MemberAuthorize;
import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.place.application.PlaceService;
import com.adregamdi.place.dto.response.GetPlaceResponse;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/place")
@RestController
public class PlaceController {
    private final PlaceService placeService;

    @GetMapping
    @MemberAuthorize
    public ResponseEntity<ApiResponse<GetPlaceResponse>> get(
            @RequestParam @PositiveOrZero final int pageNo,
            @RequestParam final String name
    ) {
        return ResponseEntity.ok()
                .body(ApiResponse.<GetPlaceResponse>builder()
                        .statusCode(HttpStatus.OK)
                        .data(placeService.get(pageNo, name))
                        .build()
                );
    }
}

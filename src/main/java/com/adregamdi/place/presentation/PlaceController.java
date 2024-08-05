package com.adregamdi.place.presentation;

import com.adregamdi.place.application.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/place")
@RestController
public class PlaceController {
    private final PlaceService placeService;
}

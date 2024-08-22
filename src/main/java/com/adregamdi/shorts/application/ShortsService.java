package com.adregamdi.shorts.application;


import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.response.CreateShortsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ShortsService {
    CreateShortsResponse uploadShorts(MultipartFile video, UUID memberId, CreateShortsRequest request);
}

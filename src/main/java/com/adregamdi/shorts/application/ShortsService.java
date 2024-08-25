package com.adregamdi.shorts.application;

import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.request.UpdateShortsRequest;

public interface ShortsService {
    void saveShorts(String memberId, CreateShortsRequest request);

    void updateShorts(String memberId, UpdateShortsRequest request);
}

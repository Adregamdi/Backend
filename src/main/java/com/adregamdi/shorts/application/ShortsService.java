package com.adregamdi.shorts.application;

import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.request.UpdateShortsRequest;
import com.adregamdi.shorts.dto.response.GetShortsResponse;

public interface ShortsService {
    GetShortsResponse getShorts(String memberId, long lastShortsId);
    void saveShorts(String memberId, CreateShortsRequest request);
    void updateShorts(String memberId, UpdateShortsRequest request);
}

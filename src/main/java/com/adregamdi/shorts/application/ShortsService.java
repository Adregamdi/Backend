package com.adregamdi.shorts.application;

import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.request.UpdateShortsRequest;
import com.adregamdi.shorts.dto.response.GetShortsResponse;

public interface ShortsService {
    GetShortsResponse getShorts(String memberId, long lastShortsId, int size);
    GetShortsResponse getUserShorts(String memberIdForTest, long lastShortsId, int size);
    void saveShorts(String memberId, CreateShortsRequest request);
    void updateShorts(String memberId, UpdateShortsRequest request);
}

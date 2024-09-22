package com.adregamdi.shorts.application;

import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.request.GetShortsByPlaceIdRequest;
import com.adregamdi.shorts.dto.request.UpdateShortsRequest;
import com.adregamdi.shorts.dto.response.GetShortsByPlaceIdResponse;
import com.adregamdi.shorts.dto.response.GetShortsResponse;
import com.adregamdi.shorts.dto.response.SaveVideoResponse;
import com.adregamdi.shorts.dto.response.UploadVideoDTO;

public interface ShortsService {
    GetShortsResponse getShorts(String memberId, long lastShortsId, int size);
    GetShortsResponse getUserShorts(String memberIdForTest, long lastShortsId, int size);
    void saveShorts(String memberId, CreateShortsRequest request);
    void updateShorts(String memberId, UpdateShortsRequest request);
    void deleteShorts(String memberId, Long shortsId);
    SaveVideoResponse saveVideo(UploadVideoDTO videoUrls, String memberId);
    String getS3KeyByShortId(Long shortsId);

    GetShortsByPlaceIdResponse getShortsByPlaceId(String memberId, GetShortsByPlaceIdRequest request);

    GetShortsResponse getHotShorts(String memberId, int lastLikeCount, int size);
}

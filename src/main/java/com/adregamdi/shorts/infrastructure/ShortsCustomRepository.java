package com.adregamdi.shorts.infrastructure;

import com.adregamdi.shorts.dto.request.GetShortsByPlaceIdRequest;
import com.adregamdi.shorts.dto.response.GetShortsByPlaceIdResponse;
import com.adregamdi.shorts.dto.response.GetShortsResponse;

public interface ShortsCustomRepository {

    GetShortsResponse getShortsForMember(String memberId, long lastId, int size);

    GetShortsResponse getUserShorts(String memberId, long lastShortsId, int size);

    GetShortsByPlaceIdResponse getShortsByPlaceId(String memberId, GetShortsByPlaceIdRequest request);

    GetShortsResponse getHotShorts(String memberId, long lastShortsId, int size);
}

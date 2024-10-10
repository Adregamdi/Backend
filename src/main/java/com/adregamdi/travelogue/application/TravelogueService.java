package com.adregamdi.travelogue.application;

import com.adregamdi.travelogue.dto.request.CreateMyTravelogueRequest;
import com.adregamdi.travelogue.dto.response.*;

public interface TravelogueService {
    CreateMyTravelogueResponse createMyTravelogue(final String currentMemberId, final CreateMyTravelogueRequest request);

    GetTravelogueResponse get(final String currentMemberId, final Long travelogueId);

    GetMyTraveloguesResponse getMyTravelogues(final String currentMemberId, final int page);

    GetMemberTraveloguesResponse getMemberTravelogues(final String memberId, final Long lastTravelogueId, final int size);

    GetRecentTraveloguesResponse getRecentTravelogues(final String currentMemberId, final int page);

    GetHotTraveloguesResponse getHotTravelogue(String currentMemberId, int lastLikeCount, int size);

    void deleteMyTravelogue(final String currentMemberId, final Long travelogueId);

    void deleteMyTravelogue(final String memberId);
}

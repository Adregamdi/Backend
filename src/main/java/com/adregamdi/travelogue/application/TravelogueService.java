package com.adregamdi.travelogue.application;

import com.adregamdi.travelogue.dto.request.CreateMyTravelogueRequest;
import com.adregamdi.travelogue.dto.response.*;

public interface TravelogueService {
    /*
     * [여행기 등록]
     * */
    CreateMyTravelogueResponse createMyTravelogue(String currentMemberId, CreateMyTravelogueRequest request);

    /*
     * [특정 여행기 조회]
     */
    GetTravelogueResponse get(String currentMemberId, Long travelogueId);

    /*
     * [내 전체 여행기 조회]
     * */
    GetMyTraveloguesResponse getMyTravelogues(String currentMemberId, int page);

    /*
     * [특정 회원 전체 여행기 조회]
     */
    GetMemberTraveloguesResponse getMemberTravelogues(String memberId, Long lastTravelogueId, int size);

    /*
     * [최근 등록된 여행기 조회]
     * */
    GetRecentTraveloguesResponse getRecentTravelogues(String currentMemberId, int page);

    /*
     * [인기있는 여행기 조회]
     * */
    GetHotTraveloguesResponse getHotTravelogue(String currentMemberId, int lastLikeCount, int size);

    /*
     * [내 특정 여행기 삭제]
     * */
    void deleteMyTravelogue(String currentMemberId, Long travelogueId);

    /*
     * [특정 회원의 여행기 전체 삭제]
     * */
    void deleteMyTravelogue(String memberId);
}

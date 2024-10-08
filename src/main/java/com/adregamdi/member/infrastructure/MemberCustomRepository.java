package com.adregamdi.member.infrastructure;

import com.adregamdi.member.dto.AllContentDTO;
import com.adregamdi.member.dto.PlaceReviewContentDTO;
import com.adregamdi.member.dto.ShortsContentDTO;
import com.adregamdi.member.dto.TravelogueContentDTO;
import com.adregamdi.member.dto.request.GetMemberContentsRequest;
import com.adregamdi.member.dto.response.GetMemberContentsResponse;

import java.util.List;

public interface MemberCustomRepository {
    GetMemberContentsResponse<List<AllContentDTO>> getMemberContentsOfAll(GetMemberContentsRequest request);

    GetMemberContentsResponse<List<TravelogueContentDTO>> getMemberContentsOfTravelogue(GetMemberContentsRequest request);
    
    GetMemberContentsResponse<List<ShortsContentDTO>> getMemberContentsOfShorts(GetMemberContentsRequest request);

    GetMemberContentsResponse<List<PlaceReviewContentDTO>> getMemberContentsOfPlaceReview(GetMemberContentsRequest request);
}

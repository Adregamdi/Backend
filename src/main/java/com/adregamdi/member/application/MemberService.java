package com.adregamdi.member.application;

import com.adregamdi.member.domain.Member;
import com.adregamdi.member.dto.request.GetMemberContentsRequest;
import com.adregamdi.member.dto.request.UpdateMyMemberRequest;
import com.adregamdi.member.dto.response.GetMemberContentsResponse;
import com.adregamdi.member.dto.response.GetMyMemberResponse;

public interface MemberService {
    void connectedAt(final Member member);

    GetMyMemberResponse getMyMember(final String memberId);

    void update(final UpdateMyMemberRequest request, final String memberId);

    void logout(final String memberId, final String accessToken);

    void delete(final String memberId);

    GetMemberContentsResponse<?> getMemberContentsOfAll(GetMemberContentsRequest request);
}

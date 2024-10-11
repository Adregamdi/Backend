package com.adregamdi.member.application;

import com.adregamdi.member.domain.Member;
import com.adregamdi.member.dto.request.GetMemberContentsRequest;
import com.adregamdi.member.dto.request.UpdateMyMemberRequest;
import com.adregamdi.member.dto.response.GetMemberContentsResponse;
import com.adregamdi.member.dto.response.GetMyMemberResponse;

public interface MemberService {
    /*
     * [마지막 접속 시간 체크]
     */
    void connectedAt(final Member member);

    /*
     * [내 정보 조회]
     */
    GetMyMemberResponse getMyMember(final String memberId);

    /*
     * [내 정보 수정]
     */
    void update(final UpdateMyMemberRequest request, final String memberId);

    /*
     * [로그아웃]
     */
    void logout(final String memberId, final String accessToken);

    /*
     * [소프트 탈퇴]
     */
    void delete(final String memberId);

    /*
     * [특정 멤버 컨텐츠 조회]
     */
    GetMemberContentsResponse<?> getMemberContentsOfAll(GetMemberContentsRequest request);
}

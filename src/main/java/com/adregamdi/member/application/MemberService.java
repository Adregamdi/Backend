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
    void connectedAt(Member member);

    /*
     * [내 정보 조회]
     */
    GetMyMemberResponse getMyMember(String memberId);

    /*
     * [내 정보 수정]
     */
    void update(UpdateMyMemberRequest request, String memberId);

    /*
     * [로그아웃]
     */
    void logout(String memberId, String accessToken);

    /*
     * [소프트 탈퇴]
     */
    void delete(String memberId);

    /*
     * [특정 멤버 컨텐츠 조회]
     */
    GetMemberContentsResponse<?> getMemberContentsOfAll(GetMemberContentsRequest request);
}

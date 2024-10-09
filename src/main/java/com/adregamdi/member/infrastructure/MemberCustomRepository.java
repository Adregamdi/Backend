package com.adregamdi.member.infrastructure;

import com.adregamdi.member.dto.AllContentDTO;
import com.adregamdi.member.dto.request.GetMemberContentsRequest;
import com.adregamdi.member.dto.response.GetMemberContentsResponse;

import java.util.List;

public interface MemberCustomRepository {
    GetMemberContentsResponse<List<AllContentDTO>> getMemberContentsOfAll(GetMemberContentsRequest request);
}

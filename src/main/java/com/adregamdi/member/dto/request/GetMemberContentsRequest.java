package com.adregamdi.member.dto.request;


import com.adregamdi.member.domain.SelectedType;

public record GetMemberContentsRequest(

        String memberId,
        SelectedType selectedType,
        Long lastContentId,
        int size
) {
}

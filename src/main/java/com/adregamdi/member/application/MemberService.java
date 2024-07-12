package com.adregamdi.member.application;

import com.adregamdi.member.domain.Member;
import com.adregamdi.member.domain.Role;
import com.adregamdi.member.exception.MemberException.MemberNotFoundException;
import com.adregamdi.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public void logout(final String memberId) {
        Member member = memberRepository.findByIdAndMemberStatus(UUID.fromString(memberId), true)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        member.updateRefreshTokenStatus(false);
    }

    @Transactional
    public void delete(final String memberId) {
        Member member = memberRepository.findByIdAndMemberStatus(UUID.fromString(memberId), true)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        member.updateAuthorization(Role.GUEST);
        member.updateMemberStatus(false);
        member.updateRefreshTokenStatus(false);
    }
}

package com.adregamdi.member.infrastructure;

import com.adregamdi.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByRefreshToken(String refreshToken);

    Optional<Member> findByIdAndMemberStatus(UUID memberId, boolean memberStatus);
}

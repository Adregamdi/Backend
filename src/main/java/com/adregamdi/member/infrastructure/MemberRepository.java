package com.adregamdi.member.infrastructure;

import com.adregamdi.member.domain.Member;
import com.adregamdi.member.domain.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String>, MemberCustomRepository {
    Optional<Member> findByRefreshToken(String refreshToken);

    Optional<Member> findByMemberIdAndMemberStatus(String memberId, boolean memberStatus);

    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String id);

    @Query("""
            SELECT m
            FROM Member m
            WHERE m.memberStatus= false
            AND m.updatedAt <= :date
            """)
    Optional<List<Member>> findByMemberStatusAndUpdatedAt(@Param("date") final LocalDateTime date);

    @Modifying
    @Query("""
            DELETE FROM Member m
            WHERE m.memberStatus= false
            AND m.updatedAt < :date
            """)
    void deleteByMemberStatusAndUpdatedAt(@Param("date") final LocalDateTime date);

    Member findByHandle(String handle);
}

package com.adregamdi.member.infrastructure;

import com.adregamdi.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Long, Member> {
}

package com.adregamdi.shorts.infrastructure;

import com.adregamdi.shorts.domain.Shorts;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShortsRepository extends JpaRepository<Shorts, Long>, ShortsCustomRepository {
    Optional<Slice<Shorts>> findAllByMemberId(Pageable pageable, UUID memberId);
}

package com.adregamdi.block.infrastructure;

import com.adregamdi.block.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    Optional<Block> findByBlockedMemberIdAndBlockingMemberId(String blockedMemberId, String blockingMemberId);
}

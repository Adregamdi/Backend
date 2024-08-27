package com.adregamdi.shorts.infrastructure;

import com.adregamdi.shorts.domain.Shorts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortsRepository extends JpaRepository<Shorts, Long>, ShortsCustomRepository {
}

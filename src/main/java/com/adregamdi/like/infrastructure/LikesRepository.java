package com.adregamdi.like.infrastructure;

import com.adregamdi.like.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Like, Long>, LikesCustomRepository {
}
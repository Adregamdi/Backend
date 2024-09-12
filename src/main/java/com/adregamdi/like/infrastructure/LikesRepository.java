package com.adregamdi.like.infrastructure;

import com.adregamdi.like.domain.Like;
import com.adregamdi.like.domain.enumtype.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Like, Long>, LikesCustomRepository {

    int countByContentTypeAndContentId(ContentType contentType, Long contentId);

    Optional<Like> findByContentTypeAndContentId(ContentType contentType, Long contentId);
}
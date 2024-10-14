package com.adregamdi.like.infrastructure;

import com.adregamdi.core.constant.ContentType;
import com.adregamdi.like.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Like, Long>, LikesCustomRepository {

    int countByContentTypeAndContentId(ContentType contentType, Long contentId);

    Optional<Like> findByMemberIdAndContentTypeAndContentId(String memberId, ContentType contentType, Long contentId);

    List<Like> findAllByMemberId(String memberId);
}
package com.adregamdi.like.application;

import com.adregamdi.like.domain.Like;
import com.adregamdi.like.domain.enumtype.ContentType;
import com.adregamdi.like.dto.AllContentDTO;
import com.adregamdi.like.dto.request.CreateLikesRequest;
import com.adregamdi.like.dto.request.DeleteLikeRequest;
import com.adregamdi.like.dto.request.GetLikesContentsRequest;
import com.adregamdi.like.dto.response.CreateShortsLikeResponse;
import com.adregamdi.like.dto.response.GetLikesContentsResponse;
import com.adregamdi.like.exception.LikesException;
import com.adregamdi.like.infrastructure.LikesRepository;
import com.adregamdi.member.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final LikesValidService likesValidService;

    public void create(String memberId, CreateLikesRequest request) {

        Like like = Like.builder()
                .memberId(UUID.fromString(memberId))
                .contentType(request.getContentType())
                .contentId(request.contentId())
                .build();

        likesRepository.save(like);
    }

    public CreateShortsLikeResponse createShortsLike(String memberId, Long shortsId) {

        Like like = Like.builder()
                .memberId(UUID.fromString(memberId))
                .contentType(ContentType.SHORTS)
                .contentId(shortsId)
                .build();

        likesRepository.save(like);

        return new CreateShortsLikeResponse(
                likesRepository.countByContentTypeAndContentId(ContentType.SHORTS, shortsId)
        );
    }


    public void delete(String memberId, Role memberRole, DeleteLikeRequest request) {

        Like like = likesRepository.findByContentTypeAndContentId(request.getContentType(), request.contentId())
                .orElseThrow(() -> new LikesException.LikesNotFoundException(request));

        if (memberRole == Role.ADMIN) {
            log.info("관리자 권한으로 좋아요를 삭제합니다. 좋아요 ID: {}", like.getLikeId());
        } else if (!likesValidService.isWriter(like.getMemberId().toString(), memberId)) {
            log.warn("작성자 외에 요청으로 인해 메서드를 종료합니다.");
            return;
        }

        likesRepository.delete(like);
    }

    public GetLikesContentsResponse<List<AllContentDTO>> getLikesContents(GetLikesContentsRequest request) {
        return likesRepository.getLikesContentsOfAll(request);
    }

    public GetLikesContentsResponse<?> getLikesContentsOfTravelogue(GetLikesContentsRequest request) {
        return likesRepository.getLikesContentsOfTravelogue(request);
    }

    public GetLikesContentsResponse<?> getLikesContentsOfPlace(GetLikesContentsRequest request) {
        return likesRepository.getLikesContentsOfPlace(request);
    }


    public GetLikesContentsResponse<?> getLikesContentsOfShorts(GetLikesContentsRequest request) {
        return likesRepository.getLikesContentsOfShorts(request);
    }
}
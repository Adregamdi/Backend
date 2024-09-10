package com.adregamdi.like.application;

import com.adregamdi.like.domain.Like;
import com.adregamdi.like.domain.enumtype.SelectedType;
import com.adregamdi.like.dto.request.CreateLikesRequest;
import com.adregamdi.like.dto.request.GetLikesContentsRequest;
import com.adregamdi.like.dto.response.GetLikesContentsResponse;
import com.adregamdi.like.exception.LikesException;
import com.adregamdi.like.infrastructure.LikesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final LikesValidService likesValidService;

    public void create(String memberId, CreateLikesRequest request) {

        Like like = Like.builder()
                .memberId(memberId)
                .contentType(request.contentType())
                .contentId(request.contentId())
                .build();

        likesRepository.save(like);
    }


    public void delete(String memberId, Long likeId) {

        Like like = likesRepository.findById(likeId)
                .orElseThrow(() -> new LikesException.LikesNotFoundException(likeId));

        if (!likesValidService.isWriter(like.getMemberId(), memberId)) {
            log.warn("작성자 외에 요청으로 인해 메서드를 종료합니다.");
            return;
        }

        likesRepository.delete(like);
        log.info("정상적으로 삭제되었습니다. likeId: {}", likeId);
    }

    public GetLikesContentsResponse<?> getLikesContents(GetLikesContentsRequest request) {
        SelectedType selectedType = request.getSelectedType();
        if (selectedType == null) {
            log.info("타입이 선택되지 않아 기본값(ALL)으로 조회합니다.");
            selectedType = SelectedType.ALL;
        }

        return switch (selectedType) {
            case ALL -> likesRepository.getLikesContentsOfAll(request);
            case SHORTS -> likesRepository.getLikesContentsOfShorts(request);
            case PLACE -> likesRepository.getLikesContentsOfPlace(request);
            case TRAVEL -> likesRepository.getLikesContentsOfTravel(request);
        };
    }
}
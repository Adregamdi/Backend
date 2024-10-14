package com.adregamdi.like.application;

import com.adregamdi.like.domain.Like;
import com.adregamdi.like.domain.enumtype.ContentType;
import com.adregamdi.like.dto.request.CreateLikesRequest;
import com.adregamdi.like.dto.request.DeleteLikeRequest;
import com.adregamdi.like.dto.request.GetLikesContentsRequest;
import com.adregamdi.like.dto.response.CreateLikesResponse;
import com.adregamdi.like.dto.response.CreateShortsLikeResponse;
import com.adregamdi.like.dto.response.GetLikesContentsResponse;
import com.adregamdi.like.exception.LikesException;
import com.adregamdi.like.infrastructure.LikesRepository;
import com.adregamdi.member.domain.Member;
import com.adregamdi.member.domain.Role;
import com.adregamdi.member.exception.MemberException;
import com.adregamdi.member.infrastructure.MemberRepository;
import com.adregamdi.notification.application.NotificationService;
import com.adregamdi.notification.domain.NotificationType;
import com.adregamdi.notification.dto.request.CreateNotificationRequest;
import com.adregamdi.shorts.domain.Shorts;
import com.adregamdi.shorts.exception.ShortsException;
import com.adregamdi.shorts.infrastructure.ShortsRepository;
import com.adregamdi.travelogue.domain.Travelogue;
import com.adregamdi.travelogue.exception.TravelogueException;
import com.adregamdi.travelogue.infrastructure.TravelogueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final LikesValidService likesValidService;
    private final NotificationService notificationService;
    private final ShortsRepository shortsRepository;
    private final TravelogueRepository travelogueRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CreateLikesResponse create(String memberId, CreateLikesRequest request) {
        boolean isLiked = likesRepository.findByMemberIdAndContentTypeAndContentId(memberId, request.getContentType(), request.contentId()).isPresent();
        if (isLiked) {
            return new CreateLikesResponse(true);
        }

        Like like = Like.builder()
                .memberId(memberId)
                .contentType(request.getContentType())
                .contentId(request.contentId())
                .build();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException.MemberNotFoundException(memberId));
        Member opponentMember = null;
        com.adregamdi.notification.domain.ContentType contentType = null;
        if (request.getContentType().equals(ContentType.SHORTS)) {
            Shorts shorts = shortsRepository.findById(request.contentId())
                    .orElseThrow(() -> new ShortsException.ShortsNotFoundException(request.contentId()));
            opponentMember = memberRepository.findById(shorts.getMemberId())
                    .orElseThrow(() -> new MemberException.MemberNotFoundException(shorts.getMemberId()));
            contentType = com.adregamdi.notification.domain.ContentType.SHORTS;
        } else if (request.getContentType().equals(ContentType.TRAVELOGUE)) {
            Travelogue travelogue = travelogueRepository.findById(request.contentId())
                    .orElseThrow(() -> new TravelogueException.TravelogueNotFoundException(request.contentId()));
            opponentMember = memberRepository.findById(travelogue.getMemberId())
                    .orElseThrow(() -> new MemberException.MemberNotFoundException(travelogue.getMemberId()));
            contentType = com.adregamdi.notification.domain.ContentType.TRAVELOGUE;
        }

        if (opponentMember != null) {
            notificationService.create(CreateNotificationRequest.of(
                    opponentMember.getMemberId(),
                    request.contentId(),
                    member.getProfile(),
                    member.getHandle(),
                    contentType,
                    NotificationType.LIKES));
        }
        return new CreateLikesResponse(likesRepository.save(like).equals(like));
    }

    @Transactional
    public CreateShortsLikeResponse createShortsLike(String memberId, Long shortsId) {
        Like like = Like.builder()
                .memberId(memberId)
                .contentType(ContentType.SHORTS)
                .contentId(shortsId)
                .build();

        likesRepository.save(like);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException.MemberNotFoundException(memberId));
        Shorts shorts = shortsRepository.findById(shortsId)
                .orElseThrow(() -> new ShortsException.ShortsNotFoundException(shortsId));
        Member opponentMember = memberRepository.findById(shorts.getMemberId())
                .orElseThrow(() -> new MemberException.MemberNotFoundException(shorts.getMemberId()));
        com.adregamdi.notification.domain.ContentType contentType = com.adregamdi.notification.domain.ContentType.SHORTS;

        notificationService.create(CreateNotificationRequest.of(
                opponentMember.getMemberId(),
                shortsId,
                member.getProfile(),
                member.getHandle(),
                contentType,
                NotificationType.LIKES));
        return new CreateShortsLikeResponse(likesRepository.countByContentTypeAndContentId(ContentType.SHORTS, shortsId));
    }


    public void delete(String memberId, Role memberRole, DeleteLikeRequest request) {

        Like like = likesRepository.findByMemberIdAndContentTypeAndContentId(memberId, request.getContentType(), request.contentId())
                .orElseThrow(() -> new LikesException.LikesNotFoundException(request));

        if (memberRole == Role.ADMIN) {
            log.info("관리자 권한으로 좋아요를 삭제합니다. 좋아요 ID: {}", like.getLikeId());
        } else if (!likesValidService.isWriter(like.getMemberId(), memberId)) {
            log.warn("작성자 외에 요청으로 인해 메서드를 종료합니다.");
            return;
        }

        likesRepository.delete(like);
        notificationService.delete(request.contentId(), request.getContentType());
    }

    @Transactional(readOnly = true)
    public GetLikesContentsResponse<?> getLikesContents(GetLikesContentsRequest request) {
        return switch (request.selectedType()) {
            case ALL -> likesRepository.getLikesContentsOfAll(request);
            case SHORTS -> likesRepository.getLikesContentsOfShorts(request);
            case TRAVELOGUE -> likesRepository.getLikesContentsOfTravelogue(request);
            case PLACE -> likesRepository.getLikesContentsOfPlace(request);
        };

    }

    @Transactional
    public Integer getLikesCount(final ContentType contentType, final Long contentId) {
        return likesRepository.countByContentTypeAndContentId(contentType, contentId);
    }

    @Transactional(readOnly = true)
    public Boolean checkIsLiked(String memberId, ContentType contentType, Long contentId) {
        return likesRepository.checkIsLiked(memberId, contentType, contentId);
    }

    public void deleteMyLike(final String memberId) {
        List<Like> likes = likesRepository.findAllByMemberId(memberId);
        if (likes.isEmpty()) {
            return;
        }
        likesRepository.deleteAll(likes);
    }
}
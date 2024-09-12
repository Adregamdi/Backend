package com.adregamdi.shorts.infrastructure;

import com.adregamdi.like.domain.enumtype.ContentType;
import com.adregamdi.shorts.dto.ShortsDTO;
import com.adregamdi.shorts.dto.request.GetShortsByPlaceIdRequest;
import com.adregamdi.shorts.dto.response.GetShortsByPlaceIdResponse;
import com.adregamdi.shorts.dto.response.GetShortsResponse;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.adregamdi.shorts.domain.QShorts.shorts;
import static com.adregamdi.place.domain.QPlace.place;
import static com.adregamdi.travelogue.domain.QTravelogue.travelogue;
import static com.adregamdi.like.domain.QLike.like;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ShortsCustomRepositoryImpl implements ShortsCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public GetShortsResponse getShortsForMember(UUID memberId, long lastId, int size) {

        // 좋아요 여부 조회
        List<ShortsDTO> content = jpaQueryFactory
                .select(Projections.constructor(ShortsDTO.class,
                        shorts.shortsId,
                        shorts.title,
                        shorts.memberId,
                        shorts.placeId,
                        place.title,
                        shorts.travelogueId,
                        travelogue.title,
                        shorts.shortsVideoUrl,
                        shorts.thumbnailUrl,
                        shorts.viewCount,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .selectOne()
                                        .from(like)
                                        .where(like.memberId.eq(memberId)
                                                .and(like.contentType.eq(ContentType.SHORTS))
                                                .and(like.contentId.eq(shorts.shortsId)))
                                        .exists(),
                                "isLiked"
                        )))
                .from(shorts)
                .leftJoin(place).on(shorts.placeId.eq(place.placeId))
                .leftJoin(travelogue).on(shorts.travelogueId.eq(travelogue.travelogueId))
                .where(
                        shorts.shortsId.gt(lastId),
                        shorts.assignedStatus.eq(true))
                .orderBy(shorts.shortsId.asc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = content.size() > size;
        if (hasNext) {
            content.remove(size);
        }

        return new GetShortsResponse(content, hasNext);
    }

    @Override
    public GetShortsResponse getUserShorts(UUID memberId, long lastShortsId, int size) {

        List<ShortsDTO> content = jpaQueryFactory
                .select(Projections.constructor(ShortsDTO.class,
                        shorts.shortsId,
                        shorts.title,
                        shorts.memberId,
                        shorts.placeId,
                        place.title,
                        shorts.travelogueId,
                        travelogue.title,
                        shorts.shortsVideoUrl,
                        shorts.thumbnailUrl,
                        shorts.viewCount,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .selectOne()
                                        .from(like)
                                        .where(like.memberId.eq(memberId)
                                                .and(like.contentType.eq(ContentType.SHORTS))
                                                .and(like.contentId.eq(shorts.shortsId)))
                                        .exists(),
                                "isLiked"
                        )))
                .from(shorts)
                .leftJoin(place).on(shorts.placeId.eq(place.placeId))
                .leftJoin(travelogue).on(shorts.travelogueId.eq(travelogue.travelogueId))
                .where(
                        shorts.shortsId.gt(lastShortsId),
                        shorts.memberId.eq(memberId),
                        shorts.assignedStatus.eq(true))
                .orderBy(shorts.createdAt.desc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = content.size() > size;
        if (hasNext) {
            content.remove(size);
        }
        return new GetShortsResponse(content, hasNext);
    }

    @Override
    public GetShortsByPlaceIdResponse getShortsByPlaceId(UUID memberId, GetShortsByPlaceIdRequest request) {

        List<ShortsDTO> contents = jpaQueryFactory
                .select(Projections.constructor(ShortsDTO.class,
                        shorts.shortsId,
                        shorts.title,
                        shorts.memberId,
                        shorts.placeId,
                        place.title,
                        shorts.travelogueId,
                        travelogue.title,
                        shorts.shortsVideoUrl,
                        shorts.thumbnailUrl,
                        shorts.viewCount,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .selectOne()
                                        .from(like)
                                        .where(like.memberId.eq(memberId)
                                                .and(like.contentType.eq(ContentType.SHORTS))
                                                .and(like.contentId.eq(shorts.shortsId)))
                                        .exists(),
                                "isLiked"
                        )))
                .from(shorts)
                .leftJoin(place).on(shorts.placeId.eq(place.placeId))
                .leftJoin(travelogue).on(shorts.travelogueId.eq(travelogue.travelogueId))
                .where(
                        shorts.shortsId.gt(request.lastShortsId()),
                        shorts.placeId.eq(request.placeId()),
                        shorts.assignedStatus.eq(true))
                .orderBy(shorts.createdAt.desc())
                .limit(request.size() + 1)
                .fetch();

        boolean hasNext = contents.size() > request.size();
        if (hasNext) {
            contents.remove(request.size());
        }
        return new GetShortsByPlaceIdResponse(contents, hasNext);
    }
}

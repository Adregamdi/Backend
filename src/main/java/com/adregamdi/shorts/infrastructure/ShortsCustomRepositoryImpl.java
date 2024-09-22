package com.adregamdi.shorts.infrastructure;

import com.adregamdi.like.domain.enumtype.ContentType;
import com.adregamdi.shorts.dto.ShortsDTO;
import com.adregamdi.shorts.dto.request.GetShortsByPlaceIdRequest;
import com.adregamdi.shorts.dto.response.GetShortsByPlaceIdResponse;
import com.adregamdi.shorts.dto.response.GetShortsResponse;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.adregamdi.like.domain.QLike.like;
import static com.adregamdi.member.domain.QMember.member;
import static com.adregamdi.place.domain.QPlace.place;
import static com.adregamdi.shorts.domain.QShorts.shorts;
import static com.adregamdi.travelogue.domain.QTravelogue.travelogue;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ShortsCustomRepositoryImpl implements ShortsCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public GetShortsResponse getShortsForMember(String memberId, long lastId, int size) {
        BooleanExpression condition = shorts.shortsId.lt(lastId).and(shorts.assignedStatus.eq(true));
        OrderSpecifier<?> orderBy = shorts.shortsId.desc();
        return getShorts(memberId, condition, orderBy, size);
    }


    @Override
    public GetShortsResponse getUserShorts(String memberId, long lastShortsId, int size) {
        BooleanExpression condition = shorts.shortsId.lt(lastShortsId)
                .and(shorts.memberId.eq(memberId))
                .and(shorts.assignedStatus.eq(true));
        OrderSpecifier<?> orderBy = shorts.shortsId.desc();
        return getShorts(memberId, condition, orderBy, size);
    }

    @Override
    public GetShortsByPlaceIdResponse getShortsByPlaceId(String memberId, GetShortsByPlaceIdRequest request) {
        BooleanExpression condition = shorts.shortsId.lt(request.lastShortsId())
                .and(shorts.placeId.eq(request.placeId()))
                .and(shorts.assignedStatus.eq(true));
        OrderSpecifier<?> orderBy = shorts.shortsId.desc();
        GetShortsResponse response = getShorts(memberId, condition, orderBy, request.size());
        return new GetShortsByPlaceIdResponse(response.shortsList(), response.hasNext());
    }

    @Override
    public GetShortsResponse getHotShorts(String memberId, long lastShortsId, int size) {

        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        BooleanExpression condition = shorts.shortsId.lt(lastShortsId)
                .and(shorts.assignedStatus.eq(true));

        List<ShortsDTO> contents = jpaQueryFactory
                .select(Projections.constructor(ShortsDTO.class,
                        shorts.shortsId,
                        shorts.title,
                        shorts.memberId,
                        member.name,
                        member.handle,
                        member.profile,
                        shorts.placeId,
                        place.title,
                        shorts.travelogueId,
                        travelogue.title,
                        shorts.shortsVideoUrl,
                        shorts.thumbnailUrl,
                        shorts.viewCount,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(like.count().intValue())
                                        .from(like)
                                        .where(like.contentId.eq(shorts.shortsId)
                                                .and(like.contentType.eq(ContentType.SHORTS))),
                                "likeCount"
                        ),
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
                .leftJoin(member).on(shorts.memberId.eq(String.valueOf(member.memberId)))
                .leftJoin(place).on(shorts.placeId.eq(place.placeId))
                .leftJoin(travelogue).on(shorts.travelogueId.eq(travelogue.travelogueId))
                .where(condition)
                .orderBy(
                        new OrderSpecifier<>(Order.DESC,
                                JPAExpressions
                                        .select(like.count())
                                        .from(like)
                                        .where(like.contentId.eq(shorts.shortsId)
                                                .and(like.contentType.eq(ContentType.SHORTS))
                                                .and(like.createdAt.after(oneMonthAgo))
                                        )
                        ),
                        shorts.shortsId.desc())
                .limit(size + 1)
                .fetch();

        return processResult(contents, size);
    }



    private GetShortsResponse getShorts(String memberId, BooleanExpression condition, OrderSpecifier<?> orderBy, int size) {

        List<ShortsDTO> contents = jpaQueryFactory
                .select(Projections.constructor(ShortsDTO.class,
                        shorts.shortsId,
                        shorts.title,
                        shorts.memberId,
                        member.name,
                        member.handle,
                        member.profile,
                        shorts.placeId,
                        place.title,
                        shorts.travelogueId,
                        travelogue.title,
                        shorts.shortsVideoUrl,
                        shorts.thumbnailUrl,
                        shorts.viewCount,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(like.count().intValue())
                                        .from(like)
                                        .where(like.contentId.eq(shorts.shortsId)
                                                .and(like.contentType.eq(ContentType.SHORTS))),
                                "likeCount"
                        ),
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
                .leftJoin(member).on(shorts.memberId.eq(String.valueOf(member.memberId)))
                .leftJoin(place).on(shorts.placeId.eq(place.placeId))
                .leftJoin(travelogue).on(shorts.travelogueId.eq(travelogue.travelogueId))
                .where(condition)
                .orderBy(orderBy)
                .limit(size + 1)
                .fetch();

        return processResult(contents, size);
    }

    private GetShortsResponse processResult(List<ShortsDTO> content, int size) {
        boolean hasNext = content.size() > size;
        if (hasNext) {
            content.remove(size);
        }
        return new GetShortsResponse(content, hasNext);
    }
}

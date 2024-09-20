package com.adregamdi.shorts.infrastructure;

import com.adregamdi.like.domain.enumtype.ContentType;
import com.adregamdi.shorts.dto.ShortsDTO;
import com.adregamdi.shorts.dto.request.GetShortsByPlaceIdRequest;
import com.adregamdi.shorts.dto.response.GetShortsByPlaceIdResponse;
import com.adregamdi.shorts.dto.response.GetShortsResponse;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import static com.adregamdi.member.domain.QMember.member;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ShortsCustomRepositoryImpl implements ShortsCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public GetShortsResponse getShortsForMember(UUID memberId, long lastId, int size) {
        BooleanExpression condition = shorts.shortsId.lt(lastId).and(shorts.assignedStatus.eq(true));
        OrderSpecifier<?> orderBy = shorts.shortsId.asc();
        return getShorts(memberId, condition, orderBy, size);
    }


    @Override
    public GetShortsResponse getUserShorts(UUID memberId, long lastShortsId, int size) {
        BooleanExpression condition = shorts.shortsId.gt(lastShortsId)
                .and(shorts.memberId.eq(memberId))
                .and(shorts.assignedStatus.eq(true));
        OrderSpecifier<?> orderBy = shorts.createdAt.desc();
        return getShorts(memberId, condition, orderBy, size);
    }

    @Override
    public GetShortsByPlaceIdResponse getShortsByPlaceId(UUID memberId, GetShortsByPlaceIdRequest request) {
        BooleanExpression condition = shorts.shortsId.gt(request.lastShortsId())
                .and(shorts.placeId.eq(request.placeId()))
                .and(shorts.assignedStatus.eq(true));
        OrderSpecifier<?> orderBy = shorts.createdAt.desc();
        GetShortsResponse response = getShorts(memberId, condition, orderBy, request.size());
        return new GetShortsByPlaceIdResponse(response.shortsList(), response.hasNext());
    }

    private GetShortsResponse getShorts(UUID memberId, BooleanExpression condition, OrderSpecifier<?> orderBy, int size) {
        List<ShortsDTO> content = jpaQueryFactory
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
                                        .selectOne()
                                        .from(like)
                                        .where(like.memberId.eq(memberId)
                                                .and(like.contentType.eq(ContentType.SHORTS))
                                                .and(like.contentId.eq(shorts.shortsId)))
                                        .exists(),
                                "isLiked"
                        )))
                .from(shorts)
                .join(member).on(shorts.memberId.eq(member.memberId))
                .leftJoin(place).on(shorts.placeId.eq(place.placeId))
                .leftJoin(travelogue).on(shorts.travelogueId.eq(travelogue.travelogueId))
                .where(condition)
                .orderBy(orderBy)
                .limit(size + 1)
                .fetch();

        return processResult(content, size);
    }

    private GetShortsResponse processResult(List<ShortsDTO> content, int size) {
        boolean hasNext = content.size() > size;
        if (hasNext) {
            content.remove(size);
        }
        return new GetShortsResponse(content, hasNext);
    }
}

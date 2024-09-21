package com.adregamdi.like.infrastructure;

import com.adregamdi.like.domain.enumtype.ContentType;
import com.adregamdi.like.dto.AllContentDTO;
import com.adregamdi.like.dto.PlaceContentDTO;
import com.adregamdi.like.dto.ShortsContentDTO;
import com.adregamdi.like.dto.TravelogueContentDTO;
import com.adregamdi.like.dto.request.GetLikesContentsRequest;
import com.adregamdi.like.dto.response.GetLikesContentsResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.adregamdi.like.domain.QLike.like;
import static com.adregamdi.member.domain.QMember.member;
import static com.adregamdi.place.domain.QPlace.place;
import static com.adregamdi.place.domain.QPlaceReview.placeReview;
import static com.adregamdi.place.domain.QPlaceReviewImage.placeReviewImage;
import static com.adregamdi.shorts.domain.QShorts.shorts;
import static com.adregamdi.travelogue.domain.QTravelogue.travelogue;
import static com.adregamdi.travelogue.domain.QTravelogueImage.travelogueImage;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikesCustomRepositoryImpl implements LikesCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public GetLikesContentsResponse<List<AllContentDTO>> getLikesContentsOfAll(GetLikesContentsRequest request) {

        List<AllContentDTO> contents = jpaQueryFactory
                .select(Projections.constructor(AllContentDTO.class,
                        Expressions.cases()
                                .when(like.contentType.eq(ContentType.SHORTS)).then(shorts.title)
                                .when(like.contentType.eq(ContentType.PLACE)).then(place.title)
                                .when(like.contentType.eq(ContentType.TRAVELOGUE)).then(travelogue.title)
                                .otherwise((String) null),
                        like.contentType,
                        like.contentId,
                        Expressions.cases()
                                .when(like.contentType.eq(ContentType.SHORTS)).then(shorts.thumbnailUrl)
                                .when(like.contentType.eq(ContentType.PLACE)).then(place.thumbnailPath)
                                .when(like.contentType.eq(ContentType.TRAVELOGUE))
                                .then(
                                        JPAExpressions.select(travelogueImage.url)
                                                .from(travelogueImage)
                                                .where(travelogueImage.travelogueId.eq(travelogue.travelogueId))
                                                .orderBy(travelogueImage.travelogueImageId.desc())
                                                .limit(1)
                                )
                                .otherwise((String) null)))
                .from(like)
                .leftJoin(shorts).on(like.contentId.eq(shorts.shortsId).and(like.contentType.eq(ContentType.SHORTS)))
                .leftJoin(place).on(like.contentId.eq(place.placeId).and(like.contentType.eq(ContentType.PLACE)))
                .leftJoin(travelogue).on(like.contentId.eq(travelogue.travelogueId).and(like.contentType.eq(ContentType.TRAVELOGUE)))
                .where(
                        like.memberId.eq(request.memberId()),
                        like.likeId.lt(request.lastLikeId()))
                .orderBy(like.likeId.desc())
                .limit(request.size() + 1)
                .fetch();

        boolean hasNext = contents.size() > request.size();
        if (hasNext) {
            contents.remove(request.size());
        }

        return new GetLikesContentsResponse<>(hasNext, contents);
    }

    @Override
    public GetLikesContentsResponse<List<ShortsContentDTO>> getLikesContentsOfShorts(GetLikesContentsRequest request) {

        List<ShortsContentDTO> contents = jpaQueryFactory
                .select(Projections.constructor(ShortsContentDTO.class,
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
                                        .where(like.memberId.eq(request.memberId())
                                                .and(like.contentType.eq(ContentType.SHORTS))
                                                .and(like.contentId.eq(shorts.shortsId)))
                                        .exists(),
                                "isLiked"
                        )))
                .from(like)
                .join(shorts).on(like.contentId.eq(shorts.shortsId).and(like.contentType.eq(ContentType.SHORTS)))
                .join(member).on(shorts.memberId.eq(String.valueOf(member.memberId)))
                .leftJoin(place).on(shorts.placeId.eq(place.placeId))
                .leftJoin(travelogue).on(shorts.travelogueId.eq(travelogue.travelogueId))
                .where(
                        like.memberId.eq(request.memberId()),
                        like.contentType.eq(ContentType.SHORTS),
                        shorts.assignedStatus.eq(true),
                        like.likeId.lt(request.lastLikeId())
                )
                .orderBy(like.likeId.desc())
                .limit(request.size() + 1)
                .fetch();

        boolean hasNext = contents.size() > request.size();
        if (hasNext) {
            contents.remove(request.size());
        }

        return new GetLikesContentsResponse<>(hasNext, contents);
    }

    @Override
    public GetLikesContentsResponse<List<TravelogueContentDTO>> getLikesContentsOfTravelogue(GetLikesContentsRequest request) {

        List<Tuple> results = jpaQueryFactory
                .select(travelogue.travelogueId, travelogue.title, member.name, member.profile)
                .from(like)
                .leftJoin(travelogue).on(like.contentId.eq(travelogue.travelogueId).and(like.contentType.eq(ContentType.TRAVELOGUE)))
                .leftJoin(member).on(travelogue.memberId.eq(String.valueOf(member.memberId)))
                .where(
                        like.memberId.eq(request.memberId()),
                        like.contentType.eq(ContentType.TRAVELOGUE),
                        like.likeId.lt(request.lastLikeId())
                )
                .orderBy(like.likeId.desc())
                .limit(request.size() + 1)
                .fetch();

        List<Long> travelogueIds = results.stream()
                .map(tuple -> tuple.get(travelogue.travelogueId))
                .collect(Collectors.toList());

        Map<Long, List<String>> imageMap = jpaQueryFactory
                .select(travelogueImage.travelogueId, travelogueImage.url)
                .from(travelogueImage)
                .where(travelogueImage.travelogueId.in(travelogueIds))
                .orderBy(travelogueImage.travelogueImageId.asc())
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(travelogueImage.travelogueId),
                        Collectors.mapping(tuple -> tuple.get(travelogueImage.url), Collectors.toList())
                ));

        List<TravelogueContentDTO> contents = results.stream()
                .map(tuple -> new TravelogueContentDTO(
                        tuple.get(travelogue.travelogueId),
                        tuple.get(travelogue.title),
                        imageMap.getOrDefault(tuple.get(travelogue.travelogueId), Collections.emptyList()).stream().limit(5).toList(),
                        tuple.get(member.name),
                        tuple.get(member.profile)
                ))
                .collect(Collectors.toList());

        boolean hasNext = contents.size() > request.size();
        if (hasNext) {
            contents = contents.subList(0, request.size());
        }

        return new GetLikesContentsResponse<>(hasNext, contents);
    }

    @Override
    public GetLikesContentsResponse<List<PlaceContentDTO>> getLikesContentsOfPlace(GetLikesContentsRequest request) {

        List<Tuple> results = jpaQueryFactory
                .select(place.placeId, place.title, place.regionLabel, place.region1Cd,
                        place.region2Cd, place.imgPath, place.thumbnailPath, like.likeId)
                .from(like)
                .join(place).on(like.contentId.eq(place.placeId).and(like.contentType.eq(ContentType.PLACE)))
                .where(
                        like.memberId.eq(request.memberId()),
                        like.contentType.eq(ContentType.PLACE),
                        like.likeId.lt(request.lastLikeId())
                )
                .orderBy(like.likeId.desc())
                .limit(request.size() + 1)
                .fetch();

        List<Long> placeIds = results.stream()
                .map(tuple -> tuple.get(place.placeId))
                .collect(Collectors.toList());

        Map<Long, List<String>> imageMap = jpaQueryFactory
                .select(placeReview.placeId, placeReviewImage.url)
                .from(placeReviewImage)
                .join(placeReview).on(placeReviewImage.placeReviewId.eq(placeReview.placeReviewId))
                .where(placeReview.placeId.in(placeIds))
                .orderBy(placeReviewImage.placeReviewImageId.desc())
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(placeReview.placeId),
                        Collectors.mapping(tuple -> tuple.get(placeReviewImage.url),
                                Collectors.collectingAndThen(Collectors.toList(),
                                        list -> list.stream().limit(5).toList()))
                ));

        Long imageReviewCount = jpaQueryFactory
                .select(placeReviewImage.count())
                .from(placeReviewImage)
                .join(placeReview).on(placeReviewImage.placeReviewId.eq(placeReview.placeReviewId))
                .where(placeReview.placeId.in(placeIds))
                .fetchOne();

        Long shortsReviewCount = jpaQueryFactory
                .select(shorts.count())
                .from(shorts)
                .where(shorts.placeId.in(placeIds))
                .fetchOne();

        List<PlaceContentDTO> contents = results.stream()
                .map(tuple -> new PlaceContentDTO(
                        tuple.get(place.placeId),
                        tuple.get(place.title),
                        tuple.get(place.regionLabel),
                        tuple.get(place.region1Cd),
                        tuple.get(place.region2Cd),
                        tuple.get(place.imgPath),
                        tuple.get(place.thumbnailPath),
                        imageMap.getOrDefault(tuple.get(place.placeId), Collections.emptyList()),
                        imageReviewCount != null ? imageReviewCount.intValue() : 0,
                        shortsReviewCount != null ? shortsReviewCount.intValue() : 0
                )).collect(Collectors.toList());

        boolean hasNext = contents.size() > request.size();
        if (hasNext) {
            contents = contents.subList(0, request.size());
        }

        return new GetLikesContentsResponse<>(hasNext, contents);
    }

    @Override
    public Boolean checkIsLiked(String memberId, ContentType contentType, Long contentId) {
        return jpaQueryFactory
                .selectFrom(like)
                .where(like.memberId.eq(memberId)
                        .and(like.contentType.eq(contentType))
                        .and(like.contentId.eq(contentId)))
                .fetchFirst() != null;
    }
}
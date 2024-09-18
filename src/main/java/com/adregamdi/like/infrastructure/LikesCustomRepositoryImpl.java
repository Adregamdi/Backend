package com.adregamdi.like.infrastructure;

import com.adregamdi.like.domain.enumtype.ContentType;
import com.adregamdi.like.dto.AllContentDTO;
import com.adregamdi.like.dto.PlaceContentDTO;
import com.adregamdi.like.dto.ShortsContentDTO;
import com.adregamdi.like.dto.TravelogueContentDTO;
import com.adregamdi.like.dto.request.GetLikesContentsRequest;
import com.adregamdi.like.dto.response.GetLikesContentsResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

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
public class LikesCustomRepositoryImpl implements LikesCustomRepository{

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
                .join(shorts).on(like.contentId.eq(shorts.shortsId).and(like.contentType.eq(ContentType.SHORTS)))
                .join(place).on(like.contentId.eq(place.placeId).and(like.contentType.eq(ContentType.PLACE)))
                .join(travelogue).on(like.contentId.eq(travelogue.travelogueId).and(like.contentType.eq(ContentType.TRAVELOGUE)))
                .where(
                        like.memberId.eq(UUID.fromString(request.memberId())),
                        like.likeId.lt(request.lastLikeId()))
                .orderBy(like.createAt.desc())
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
                        shorts.shortsVideoUrl,
                        shorts.thumbnailUrl
                ))
                .from(like)
                .join(shorts).on(like.contentId.eq(shorts.shortsId).and(like.contentType.eq(ContentType.SHORTS)))
                .where(
                        like.memberId.eq(UUID.fromString(request.memberId())),
                        like.contentType.eq(ContentType.SHORTS),
                        like.likeId.lt(request.lastLikeId())
                )
                .orderBy(like.createAt.desc())
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

        List<TravelogueContentDTO> contents = jpaQueryFactory
                .select(Projections.constructor(TravelogueContentDTO.class,
                        travelogue.travelogueId,
                        travelogue.title,
                        JPAExpressions
                                .select(travelogueImage.url)
                                .from(travelogueImage)
                                .where(travelogueImage.travelogueId.eq(travelogue.travelogueId))
                                .orderBy(travelogueImage.travelogueImageId.asc())
                                .limit(5),
                        member.name,
                        member.profile
                ))
                .from(like)
                .join(travelogue).on(like.contentId.eq(travelogue.travelogueId).and(like.contentType.eq(ContentType.TRAVELOGUE)))
                .join(member).on(travelogue.memberId.eq(member.memberId))
                .where(
                        like.memberId.eq(UUID.fromString(request.memberId())),
                        like.contentType.eq(ContentType.TRAVELOGUE),
                        like.likeId.lt(request.lastLikeId())
                )
                .groupBy(travelogue.travelogueId, travelogue.title, member.name, member.profile)
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
    public GetLikesContentsResponse<List<PlaceContentDTO>> getLikesContentsOfPlace(GetLikesContentsRequest request) {

        List<PlaceContentDTO> contents = jpaQueryFactory
                .select(Projections.constructor(PlaceContentDTO.class,
                        place.placeId,
                        place.title,
                        place.regionLabel,
                        place.region1Cd,
                        place.region2Cd,
                        place.imgPath,
                        place.thumbnailPath,
                        JPAExpressions
                                .select(placeReviewImage.url)
                                .from(placeReviewImage)
                                .join(placeReview).on(placeReviewImage.placeReviewId.eq(placeReview.placeReviewId))
                                .where(placeReview.placeId.eq(place.placeId))
                                .orderBy(placeReviewImage.placeReviewImageId.desc())
                                .limit(5),
                        JPAExpressions
                                .select(placeReviewImage.count())
                                .from(placeReviewImage)
                                .join(placeReview).on(placeReviewImage.placeReviewId.eq(placeReview.placeReviewId))
                                .where(placeReview.placeId.eq(place.placeId)),
                        JPAExpressions
                                .select(shorts.count())
                                .from(shorts)
                                .where(shorts.placeId.eq(place.placeId))
                ))
                .from(like)
                .join(place).on(like.contentId.eq(place.placeId).and(like.contentType.eq(ContentType.PLACE)))
                .where(
                        like.memberId.eq(UUID.fromString(request.memberId())),
                        like.contentType.eq(ContentType.PLACE),
                        like.likeId.lt(request.lastLikeId())
                )
                .groupBy(place.placeId, place.title, place.regionLabel, place.region1Cd,
                        place.region2Cd, place.imgPath, place.thumbnailPath)
                .orderBy(like.createAt.desc())
                .limit(request.size() + 1)
                .fetch();

        boolean hasNext = contents.size() > request.size();
        if (hasNext) {
            contents.remove(request.size());
        }

        return new GetLikesContentsResponse<>(hasNext, contents);
    }
}
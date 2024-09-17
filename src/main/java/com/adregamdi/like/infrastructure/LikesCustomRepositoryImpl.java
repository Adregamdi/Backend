package com.adregamdi.like.infrastructure;

import com.adregamdi.like.domain.enumtype.ContentType;
import com.adregamdi.like.dto.AllContentDTO;
import com.adregamdi.like.dto.PlaceContentDTO;
import com.adregamdi.like.dto.ShortsContentDTO;
import com.adregamdi.like.dto.TravelogueContentDTO;
import com.adregamdi.like.dto.request.GetLikesContentsRequest;
import com.adregamdi.like.dto.response.GetLikesContentsResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.adregamdi.like.domain.QLike.like;
import static com.adregamdi.member.domain.QMember.member;
import static com.adregamdi.place.domain.QPlace.place;
import static com.adregamdi.shorts.domain.QShorts.shorts;
import static com.adregamdi.travelogue.domain.QTravelogue.travelogue;
import static com.adregamdi.travelogue.domain.QTravelogueImage.travelogueImage;
import static com.adregamdi.place.domain.QPlaceReview.placeReview;
import static com.adregamdi.place.domain.QPlaceReviewImage.placeReviewImage;

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
                .leftJoin(shorts).on(like.contentId.eq(shorts.shortsId).and(like.contentType.eq(ContentType.SHORTS)))
                .leftJoin(place).on(like.contentId.eq(place.placeId).and(like.contentType.eq(ContentType.PLACE)))
                .leftJoin(travelogue).on(like.contentId.eq(travelogue.travelogueId).and(like.contentType.eq(ContentType.TRAVELOGUE)))
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
                        like.likeId.gt(request.lastLikeId())
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

        List<Tuple> results = jpaQueryFactory
                .select(
                        travelogue.travelogueId,
                        travelogue.title,
                        member.name,
                        member.profile,
                        travelogueImage.url
                )
                .from(like)
                .leftJoin(travelogue).on(like.contentId.eq(travelogue.travelogueId).and(like.contentType.eq(ContentType.TRAVELOGUE)))
                .leftJoin(member).on(travelogue.memberId.eq(member.memberId))
                .leftJoin(travelogueImage).on(travelogue.travelogueId.eq(travelogueImage.travelogueId))
                .where(
                        like.memberId.eq(UUID.fromString(request.memberId())),
                        like.contentType.eq(ContentType.TRAVELOGUE),
                        like.likeId.gt(request.lastLikeId()))
                .orderBy(like.likeId.desc(), travelogueImage.travelogueImageId.desc())
                .fetch();

        Map<Long, TravelogueContentDTO> contentMap = new LinkedHashMap<>();
        for (Tuple tuple : results) {
            Long travelogueId = tuple.get(travelogue.travelogueId);
            TravelogueContentDTO dto = contentMap.get(travelogueId);
            if (dto == null) {
                dto = new TravelogueContentDTO(
                        travelogueId,
                        tuple.get(travelogue.title),
                        new ArrayList<>(),
                        tuple.get(member.name),
                        tuple.get(member.profile)
                );
                contentMap.put(travelogueId, dto);
            }
            String imageUrl = tuple.get(travelogueImage.url);
            if (imageUrl != null && dto.getImageList().size() < 5) {
                dto.getImageList().add(imageUrl);
            }
        }

        List<TravelogueContentDTO> contents = new ArrayList<>(contentMap.values());

        boolean hasNext = contents.size() > request.size();
        if (hasNext) {
            contents = contents.subList(0, request.size());
        }

        return new GetLikesContentsResponse<>(hasNext, contents);
    }

    @Override
    public GetLikesContentsResponse<List<PlaceContentDTO>> getLikesContentsOfPlace(GetLikesContentsRequest request) {

        List<Tuple> results = jpaQueryFactory
                .select(
                        place.placeId,
                        place.title,
                        place.contentsLabel,
                        place.regionLabel,
                        place.region1Cd,
                        place.region2Cd,
                        place.tag,
                        place.imgPath,
                        place.thumbnailPath,
                        placeReviewImage.url,
                        placeReviewImage.placeReviewImageId,
                        JPAExpressions.select(placeReviewImage.count())
                                .from(placeReviewImage)
                                .join(placeReview).on(placeReviewImage.placeReviewId.eq(placeReview.placeReviewId))
                                .where(placeReview.placeId.eq(place.placeId)),
                        JPAExpressions.select(shorts.count())
                                .from(shorts)
                                .where(shorts.placeId.eq(place.placeId))
                )
                .from(like)
                .join(place).on(like.contentId.eq(place.placeId).and(like.contentType.eq(ContentType.PLACE)))
                .leftJoin(placeReview).on(place.placeId.eq(placeReview.placeId))
                .leftJoin(placeReviewImage).on(placeReview.placeReviewId.eq(placeReviewImage.placeReviewId))
                .where(
                        like.memberId.eq(UUID.fromString(request.memberId())),
                        like.contentType.eq(ContentType.PLACE),
                        like.likeId.gt(request.lastLikeId()))
                .orderBy(like.createAt.desc(), placeReviewImage.placeReviewImageId.desc())
                .fetch();

        Map<Long, PlaceContentDTO> contentMap = new LinkedHashMap<>();
        for (Tuple tuple : results) {
            Long placeId = tuple.get(place.placeId);
            PlaceContentDTO dto = contentMap.get(placeId);
            if (dto == null) {
                dto = new PlaceContentDTO(
                        placeId,
                        tuple.get(place.title),
                        tuple.get(place.contentsLabel),
                        tuple.get(place.regionLabel),
                        tuple.get(place.region1Cd),
                        tuple.get(place.region2Cd),
                        tuple.get(place.tag),
                        tuple.get(place.imgPath),
                        tuple.get(place.thumbnailPath),
                        new ArrayList<>(),
                        tuple.get("imageReviewCnt", String.class),
                        tuple.get("shortsReviewCnt", String.class)
                );
                contentMap.put(placeId, dto);
            }
            String imageUrl = tuple.get(placeReviewImage.url);
            if (imageUrl != null && dto.getImageList().size() < 5) {
                dto.getImageList().add(imageUrl);
            }
        }

        List<PlaceContentDTO> contents = new ArrayList<>(contentMap.values());

        boolean hasNext = contents.size() > request.size();
        if (hasNext) {
            contents = contents.subList(0, request.size());
        }

        return new GetLikesContentsResponse<>(hasNext, contents);
    }
}
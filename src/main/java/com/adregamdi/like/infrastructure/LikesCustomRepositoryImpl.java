package com.adregamdi.like.infrastructure;

import com.adregamdi.like.domain.enumtype.ContentType;
import com.adregamdi.like.dto.AllContentDTO;
import com.adregamdi.like.dto.PlaceContentDTO;
import com.adregamdi.like.dto.ShortsContentDTO;
import com.adregamdi.like.dto.TravelContentDTO;
import com.adregamdi.like.dto.request.GetLikesContentsRequest;
import com.adregamdi.like.dto.response.GetLikesContentsResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.adregamdi.like.domain.QLike.like;
import static com.adregamdi.place.domain.QPlace.place;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeCustomRepositoryImpl implements LikesCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    //    @Override
    public GetLikesContentsResponse<List<AllContentDTO>> getLikesContentsOfAll(GetLikesContentsRequest request) {
//        List<AllContentDTO> contents = jpaQueryFactory
//                .select(Projections.constructor(AllContentDTO.class,
//                        Expressions.cases()
//                                .when(like.contentType.eq(ContentType.SHORTS)).then(shorts.title)
//                                .when(like.contentType.eq(ContentType.PLACE)).then(place.title)
//                                .when(like.contentType.eq(ContentType.TRAVEL)).then(schedule.title)
//                                .otherwise((String) null),
//                        like.contentType,
//                        like.contentId,
//                        Expressions.cases()
//                                .when(like.contentType.eq(ContentType.SHORTS)).then(shorts.thumbnailUrl)
//                                .when(like.contentType.eq(ContentType.PLACE)).then(place.thumbnailPath)
//                                .otherwise((String) null)))
//                .from(like)
//                .leftJoin(shorts).on(like.contentId.eq(shorts.shortsId).and(like.contentType.eq(ContentType.SHORTS)))
//                .leftJoin(place).on(like.contentId.eq(place.placeId).and(like.contentType.eq(ContentType.PLACE)))
//                .leftJoin(schedule).on(like.contentId.eq(schedule.scheduleId).and(like.contentType.eq(ContentType.TRAVEL)))
//                .where(
//                        like.memberId.eq(request.memberId()),
//                        like.likeId.gt(request.lastLikeId()))
//                .orderBy(like.createAt.desc())
//                .limit(request.size() + 1)
//                .fetch();
//
//        boolean hasNext = contents.size() > request.size();
//        if (hasNext) {
//            contents.remove(request.size());
//        }
//
//        return new GetLikesContentsResponse<>(request.getSelectedType(), hasNext, contents);
        return null;
    }

    //    @Override
    public GetLikesContentsResponse<List<ShortsContentDTO>> getLikesContentsOfShorts(GetLikesContentsRequest request) {
//        List<ShortsContentDTO> contents = jpaQueryFactory
//                .select(Projections.constructor(ShortsContentDTO.class,
//                        shorts.shortsId,
//                        shorts.title,
//                        shorts.memberId,
//                        shorts.placeId,
//                        place.title.as("placeName"),
//                        shorts.travelReviewId,
//                        schedule.title.as("travelTitle"),
//                        shorts.shortsVideoUrl,
//                        shorts.thumbnailUrl,
//                        shorts.viewCount,
//                        Expressions.constant(true)))
//                .from(like)
//                .join(shorts).on(like.contentId.eq(shorts.shortsId))
//                .leftJoin(place).on(shorts.placeId.eq(place.placeId))
//                .leftJoin(schedule).on(shorts.travelReviewId.eq(schedule.scheduleId))
//                .where(
//                        like.memberId.eq(request.memberId()),
//                        like.contentType.eq(ContentType.SHORTS),
//                        like.likeId.gt(request.lastLikeId())
//                )
//                .orderBy(like.createAt.desc())
//                .limit(request.size() + 1)
//                .fetch();
//
//        boolean hasNext = contents.size() > request.size();
//        if (hasNext) {
//            contents.remove(request.size());
//        }
//
//        return new GetLikesContentsResponse<>(request.getSelectedType(), hasNext, contents);
        return null;
    }

    //    @Override
    public GetLikesContentsResponse<List<TravelContentDTO>> getLikesContentsOfTravel(GetLikesContentsRequest request) {
        // 여행기 도메인 완료 후 작성
        return null;
    }

    //    @Override
    public GetLikesContentsResponse<List<PlaceContentDTO>> getLikesContentsOfPlace(GetLikesContentsRequest request) {

        // 추후에 장소 리뷰에 대한 사진들 또한 가져와야 함.
        List<PlaceContentDTO> contents = jpaQueryFactory
                .select(Projections.constructor(PlaceContentDTO.class,
                        place.placeId,
                        place.title,
                        place.contentsLabel,
                        place.region1Cd,
                        place.region2Cd,
                        place.address,
                        place.roadAddress,
                        place.tag,
                        place.introduction,
                        place.information,
                        place.latitude,
                        place.longitude,
                        place.phoneNo,
                        place.imgPath,
                        place.thumbnailPath
                ))
                .from(like)
                .join(place).on(like.contentId.eq(place.placeId))
                .where(
                        like.memberId.eq(request.memberId()),
                        like.contentType.eq(ContentType.PLACE),
                        like.likeId.gt(request.lastLikeId()))
                .orderBy(like.createAt.desc())
                .limit(request.size() + 1)
                .fetch();


        boolean hasNext = contents.size() > request.size();
        if (hasNext) {
            contents.remove(request.size());
        }

        return new GetLikesContentsResponse<>(request.getSelectedType(), hasNext, contents);
    }
}
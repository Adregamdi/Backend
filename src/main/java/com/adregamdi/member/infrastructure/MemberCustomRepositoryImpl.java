package com.adregamdi.member.infrastructure;

import com.adregamdi.member.dto.AllContentDTO;
import com.adregamdi.member.dto.request.GetMemberContentsRequest;
import com.adregamdi.member.dto.response.GetMemberContentsResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.adregamdi.place.domain.QPlaceReview.placeReview;
import static com.adregamdi.place.domain.QPlaceReviewImage.placeReviewImage;
import static com.adregamdi.shorts.domain.QShorts.shorts;
import static com.adregamdi.travelogue.domain.QTravelogue.travelogue;
import static com.adregamdi.travelogue.domain.QTravelogueImage.travelogueImage;


@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public GetMemberContentsResponse<List<AllContentDTO>> getMemberContentsOfAll(GetMemberContentsRequest request) {

        // Travelogue 결과
        List<AllContentDTO> travelogueResults = jpaQueryFactory
                .select(Projections.constructor(AllContentDTO.class,
                        Expressions.constant("TRAVELOGUE"),
                        travelogue.travelogueId,
                        Expressions.stringTemplate("MIN({0})", travelogueImage.url),
                        travelogue.createdAt
                ))
                .from(travelogue)
                .leftJoin(travelogueImage)
                .on(travelogue.travelogueId.eq(travelogueImage.travelogueId))
                .where(
                        travelogue.memberId.eq(request.memberId()),
                        travelogue.createdAt.lt(request.createAt()))
                .groupBy(travelogue.travelogueId)
                .fetch();

        // Shorts 결과
        List<AllContentDTO> shortsResults = jpaQueryFactory
                .select(Projections.constructor(AllContentDTO.class,
                        Expressions.constant("SHORTS"),
                        shorts.shortsId,
                        shorts.thumbnailUrl,
                        shorts.createdAt
                ))
                .from(shorts)
                .where(
                        shorts.memberId.eq(request.memberId()),
                        shorts.createdAt.lt(request.createAt()))
                .fetch();

        // PlaceReview 결과
        List<AllContentDTO> placeReviewResults = jpaQueryFactory
                .select(Projections.constructor(AllContentDTO.class,
                        Expressions.constant("PLACE_REVIEW"),
                        placeReview.placeId,
                        Expressions.stringTemplate("MIN({0})", placeReviewImage.url),
                        placeReview.createdAt
                ))
                .from(placeReview)
                .leftJoin(placeReviewImage)
                .on(placeReview.placeReviewId.eq(placeReviewImage.placeReviewId))
                .where(
                        placeReview.memberId.eq(request.memberId()),
                        placeReview.createdAt.lt(request.createAt()))
                .groupBy(placeReview.placeReviewId)
                .fetch();

        // 모든 결과 합치기
        List<AllContentDTO> allContents = new ArrayList<>();
        allContents.addAll(travelogueResults);
        allContents.addAll(shortsResults);
        allContents.addAll(placeReviewResults);

        // createdAt 기준으로 정렬 (최신순)
        allContents.sort(Comparator.comparing(AllContentDTO::getCreatedAt).reversed());

        int size = request.size();
        boolean hasNext = allContents.size() > size;
        List<AllContentDTO> pagedContents =
                hasNext ? allContents.subList(0, size) : allContents;

        return new GetMemberContentsResponse<>(hasNext, pagedContents);
    }

}

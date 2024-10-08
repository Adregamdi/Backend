package com.adregamdi.member.infrastructure;

import com.adregamdi.member.dto.AllContentDTO;
import com.adregamdi.member.dto.PlaceReviewContentDTO;
import com.adregamdi.member.dto.ShortsContentDTO;
import com.adregamdi.member.dto.TravelogueContentDTO;
import com.adregamdi.member.dto.request.GetMemberContentsRequest;
import com.adregamdi.member.dto.response.GetMemberContentsResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.adregamdi.member.domain.QMember.member;
import static com.adregamdi.travelogue.domain.QTravelogue.travelogue;
import static com.adregamdi.travelogue.domain.QTravelogueImage.travelogueImage;
import static com.adregamdi.shorts.domain.QShorts.shorts;
import static com.adregamdi.place.domain.QPlaceReview.placeReview;
import static com.adregamdi.place.domain.QPlaceReviewImage.placeReviewImage;



@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public GetMemberContentsResponse<List<AllContentDTO>> getMemberContentsOfAll(GetMemberContentsRequest request) {

        List<Tuple> results = jpaQueryFactory
                .select(

                )
                .from(member)
                .leftJoin(travelogue).on(member.memberId.eq(travelogue.memberId))
                .leftJoin(travelogueImage).on(travelogue.travelogueId.eq(travelogueImage.travelogueId))
                .leftJoin(shorts).on(member.memberId.eq(shorts.memberId))
                .leftJoin(placeReview).on(member.memberId.eq(placeReview.memberId))
                .leftJoin(placeReviewImage).on(placeReview.placeReviewId.eq(placeReviewImage.placeReviewId))
                .where(
                        travelogue.createdAt.lt(request.createAt()),
                        shorts.createdAt.lt(request.createAt()),
                        placeReview.createdAt.lt(request.createAt()))
                .orderBy(shorts.createdAt.desc())
        return null;
    }

    private OrderSpecifier<?> getOrder (LocalDateTime localDateTime1, LocalDateTime localDateTime2, LocalDateTime localDateTime3) {

    }

    @Override
    public GetMemberContentsResponse<List<TravelogueContentDTO>> getMemberContentsOfTravelogue(GetMemberContentsRequest request) {
        return null;
    }

    @Override
    public GetMemberContentsResponse<List<ShortsContentDTO>> getMemberContentsOfShorts(GetMemberContentsRequest request) {
        return null;
    }

    @Override
    public GetMemberContentsResponse<List<PlaceReviewContentDTO>> getMemberContentsOfPlaceReview(GetMemberContentsRequest request) {
        return null;
    }
}

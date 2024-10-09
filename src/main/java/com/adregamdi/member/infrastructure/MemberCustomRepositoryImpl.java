package com.adregamdi.member.infrastructure;

import com.adregamdi.member.dto.AllContentDTO;
import com.adregamdi.member.dto.request.GetMemberContentsRequest;
import com.adregamdi.member.dto.response.GetMemberContentsResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

        // 필요한 Expression 정의
        StringPath contentType = Expressions.stringPath("contentType");
        NumberPath<Long> contentId = Expressions.numberPath(Long.class, "contentId");
        StringPath imageUrl = Expressions.stringPath("imageUrl");
        DateTimePath<LocalDateTime> createdAt = Expressions.dateTimePath(LocalDateTime.class, "createdAt");

        List<Tuple> travelogueResults = jpaQueryFactory
                .select(
                        Expressions.constant("TRAVELOGUE"),
                        travelogue.travelogueId,
                        Expressions.stringTemplate("MIN({0})", travelogueImage.url).as(imageUrl),
                        travelogue.createdAt)
                .from(travelogue)
                .leftJoin(travelogueImage)
                    .on(travelogue.travelogueId.eq(travelogueImage.travelogueId))
                .where(
                        travelogue.memberId.eq(request.memberId()),
                        travelogue.createdAt.lt(request.createAt()))
                .groupBy(travelogue.travelogueId)
                .fetch();

        List<Tuple> shortsResults = jpaQueryFactory
                .select(
                        Expressions.constant("SHORTS"),
                        shorts.shortsId,
                        shorts.thumbnailUrl,
                        shorts.createdAt)
                .from(shorts)
                .where(
                        shorts.memberId.eq(request.memberId()),
                        shorts.createdAt.lt(request.createAt()))
                .fetch();

        List<Tuple> placeReviewResults = jpaQueryFactory
                .select(
                        Expressions.constant("PLACE_REVIEW"),
                        placeReview.placeReviewId,
                        Expressions.stringTemplate("MIN({0})", placeReviewImage.url).as(imageUrl),
                        placeReview.createdAt)
                .from(placeReview)
                .leftJoin(placeReviewImage)
                    .on(placeReview.placeReviewId.eq(placeReviewImage.placeReviewId))
                .where(
                        placeReview.memberId.eq(request.memberId()),
                        placeReview.createdAt.lt(request.createAt()))
                .groupBy(placeReview.placeReviewId)
                .fetch();

        List<AllContentDTO> allContents = new ArrayList<>();

        for (Tuple tuple : travelogueResults) {
            AllContentDTO dto = new AllContentDTO(
                    tuple.get(contentType),
                    tuple.get(contentId),
                    tuple.get(imageUrl),
                    tuple.get(createdAt)
            );
            allContents.add(dto);
        }

        for (Tuple tuple : shortsResults) {
            AllContentDTO dto = new AllContentDTO(
                    tuple.get(contentType),
                    tuple.get(contentId),
                    tuple.get(imageUrl),
                    tuple.get(createdAt)
            );
            allContents.add(dto);
        }

        for (Tuple tuple : placeReviewResults) {
            AllContentDTO dto = new AllContentDTO(
                    tuple.get(contentType),
                    tuple.get(contentId),
                    tuple.get(imageUrl),
                    tuple.get(createdAt)
            );
            allContents.add(dto);
        }

        // createdAt 기준으로 정렬 (최신순)
        allContents.sort(Comparator.comparing(AllContentDTO::getCreatedAt).reversed());

        int size = request.size();
        boolean hasNext = allContents.size() > size;
        List<AllContentDTO> pagedContents =
                hasNext ? allContents.subList(0, size) : allContents;

        return new GetMemberContentsResponse<>(hasNext, pagedContents);
    }

}

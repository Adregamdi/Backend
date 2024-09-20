package com.adregamdi.search.infrastructure;

import com.adregamdi.like.domain.enumtype.ContentType;
import com.adregamdi.search.dto.PlaceSearchDTO;
import com.adregamdi.search.dto.ShortsSearchDTO;
import com.adregamdi.search.dto.TravelogueSearchDTO;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static com.adregamdi.core.utils.RepositoryUtil.makeOrderSpecifiers;
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
public class SearchRepositoryImpl implements SearchRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<TravelogueSearchDTO> searchTravelogues(final String keyword, final Pageable pageable) {
        List<TravelogueSearchDTO> results = queryFactory
                .select(Projections.constructor(TravelogueSearchDTO.class,
                        travelogue.travelogueId,
                        travelogue.title,
                        member.profile,
                        member.handle,
                        Expressions.constant(new ArrayList<String>())))
                .from(travelogue)
                .leftJoin(member).on(travelogue.memberId.eq(member.memberId))
                .where(travelogue.title.startsWith(keyword))
                .orderBy(makeOrderSpecifiers(travelogue, pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        List<TravelogueSearchDTO> content = hasNext ? results.subList(0, pageable.getPageSize()) : results;

        content = fetchAndSetTravelogueImageUrls(content);

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private List<TravelogueSearchDTO> fetchAndSetTravelogueImageUrls(List<TravelogueSearchDTO> travelogues) {
        if (travelogues.isEmpty()) {
            return travelogues;
        }

        List<Long> travelogueIds = travelogues.stream()
                .map(TravelogueSearchDTO::travelogueId)
                .collect(Collectors.toList());

        Map<Long, List<String>> imageUrlMap = queryFactory
                .select(travelogueImage.travelogueId, travelogueImage.url)
                .from(travelogueImage)
                .where(travelogueImage.travelogueId.in(travelogueIds))
                .orderBy(travelogueImage.travelogueImageId.desc()) // 내림차순 정렬 추가
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(travelogueImage.travelogueId),
                        LinkedHashMap::new, // 순서 유지를 위해 LinkedHashMap 사용
                        Collectors.mapping(tuple -> tuple.get(travelogueImage.url), Collectors.toList())
                ));

        return travelogues.stream()
                .map(dto -> new TravelogueSearchDTO(
                        dto.travelogueId(),
                        dto.title(),
                        dto.profile(),
                        dto.memberHandle(),
                        imageUrlMap.getOrDefault(dto.travelogueId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Slice<ShortsSearchDTO> searchShorts(final String keyword, final Pageable pageable, final UUID memberId) {
        List<ShortsSearchDTO> results = queryFactory
                .select(Projections.constructor(ShortsSearchDTO.class,
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
                .join(member).on(shorts.memberId.eq(member.memberId))
                .leftJoin(place).on(shorts.placeId.eq(place.placeId))
                .leftJoin(travelogue).on(shorts.travelogueId.eq(travelogue.travelogueId))
                .where(shorts.title.startsWith(keyword),
                        shorts.assignedStatus.eq(true))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        List<ShortsSearchDTO> content = hasNext ? results.subList(0, pageable.getPageSize()) : results;

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<PlaceSearchDTO> searchPlaces(final String keyword, final Pageable pageable) {
        List<PlaceSearchDTO> results = queryFactory
                .select(Projections.constructor(PlaceSearchDTO.class,
                        place.placeId,
                        place.title,
                        place.contentsLabel,
                        place.regionLabel,
                        place.roadAddress,
                        JPAExpressions.select(placeReview.count()).from(placeReview)
                                .where(placeReview.placeId.eq(place.placeId)),
                        JPAExpressions.select(shorts.count()).from(shorts)
                                .where(shorts.placeId.eq(place.placeId)),
                        Expressions.constant(new ArrayList<String>())))
                .from(place)
                .where(place.title.startsWith(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        List<PlaceSearchDTO> content = hasNext ? results.subList(0, pageable.getPageSize()) : results;

        content = fetchPlaceImageUrls(content);

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private List<PlaceSearchDTO> fetchPlaceImageUrls(List<PlaceSearchDTO> places) {
        // Fetch Place imgPaths
        Map<Long, String> placeImgPathMap = queryFactory
                .select(place.placeId, place.imgPath)
                .from(place)
                .where(place.placeId.in(places.stream().map(PlaceSearchDTO::placeId).collect(Collectors.toList())))
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(place.placeId),
                        tuple -> tuple.get(place.imgPath)
                ));

        // Fetch PlaceReviewImage urls
        Map<Long, List<String>> imageUrlMap = queryFactory
                .select(placeReview.placeId, placeReviewImage.url)
                .from(placeReviewImage)
                .join(placeReview).on(placeReviewImage.placeReviewId.eq(placeReview.placeReviewId))
                .where(placeReview.placeId.in(places.stream().map(PlaceSearchDTO::placeId).collect(Collectors.toList())))
                .orderBy(placeReviewImage.placeReviewImageId.desc())
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(placeReview.placeId),
                        Collectors.mapping(tuple -> tuple.get(placeReviewImage.url), Collectors.toList())
                ));

        return places.stream()
                .map(dto -> {
                    List<String> imageUrls = new ArrayList<>(imageUrlMap.getOrDefault(dto.placeId(), Collections.emptyList()));
                    String placeImgPath = placeImgPathMap.get(dto.placeId());
                    if (placeImgPath != null && !placeImgPath.isEmpty()) {
                        imageUrls.add(placeImgPath);
                    }
                    return new PlaceSearchDTO(
                            dto.placeId(),
                            dto.title(),
                            dto.contentsLabel(),
                            dto.regionLabel(),
                            dto.roadAddress(),
                            dto.photoReviewCount(),
                            dto.shortsCount(),
                            imageUrls
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public long countTravelogues(final String keyword) {
        return executeCountQuery(travelogue, travelogue.title.startsWith(keyword));
    }

    @Override
    public long countShorts(final String keyword) {
        return executeCountQuery(shorts, shorts.title.startsWith(keyword));
    }

    @Override
    public long countPlaces(final String keyword) {
        return executeCountQuery(place, place.title.startsWith(keyword));
    }

    private long executeCountQuery(final EntityPathBase<?> entity, final Predicate condition) {
        Long count = queryFactory
                .select(entity.count())
                .from(entity)
                .where(condition)
                .fetchOne();
        return count != null ? count : 0L;
    }
}
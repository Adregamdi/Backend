package com.adregamdi.search.infrastructure;

import com.adregamdi.search.dto.PlaceSearchDTO;
import com.adregamdi.search.dto.ShortsSearchDTO;
import com.adregamdi.search.dto.TravelogueSearchDTO;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.adregamdi.core.utils.RepositoryUtil.makeOrderSpecifiers;
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
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(travelogueImage.travelogueId),
                        Collectors.mapping(tuple -> tuple.get(travelogueImage.url), Collectors.toList())
                ));

        return travelogues.stream()
                .map(dto -> new TravelogueSearchDTO(
                        dto.travelogueId(),
                        dto.title(),
                        dto.memberHandle(),
                        imageUrlMap.getOrDefault(dto.travelogueId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Slice<ShortsSearchDTO> searchShorts(final String keyword, final Pageable pageable) {
        List<ShortsSearchDTO> results = queryFactory
                .select(Projections.constructor(ShortsSearchDTO.class,
                        shorts.shortsId,
                        shorts.title,
                        shorts.thumbnailUrl))
                .from(shorts)
                .where(shorts.title.startsWith(keyword))
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
                        Expressions.constant(new ArrayList<String>()),
                        JPAExpressions.select(placeReview.count()).from(placeReview)
                                .where(placeReview.placeId.eq(place.placeId)),
                        JPAExpressions.select(shorts.count()).from(shorts)
                                .where(shorts.placeId.eq(place.placeId))))
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
        Map<Long, List<String>> imageUrlMap = queryFactory
                .select(placeReview.placeId, placeReviewImage.url)
                .from(placeReviewImage)
                .join(placeReview).on(placeReviewImage.placeReviewId.eq(placeReview.placeReviewId))
                .where(placeReview.placeId.in(places.stream().map(PlaceSearchDTO::placeId).collect(Collectors.toList())))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(placeReview.placeId),
                        Collectors.mapping(tuple -> tuple.get(placeReviewImage.url), Collectors.toList())
                ));

        return places.stream()
                .map(dto -> new PlaceSearchDTO(
                        dto.placeId(),
                        dto.title(),
                        dto.contentsLabel(),
                        dto.regionLabel(),
                        imageUrlMap.getOrDefault(dto.placeId(), Collections.emptyList()),
                        dto.photoReviewCount(),
                        dto.shortsCount()
                ))
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
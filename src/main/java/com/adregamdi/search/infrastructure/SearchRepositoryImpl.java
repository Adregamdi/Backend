package com.adregamdi.search.infrastructure;

import com.adregamdi.search.dto.PlaceSearchDTO;
import com.adregamdi.search.dto.ShortsSearchDTO;
import com.adregamdi.search.dto.TravelogueSearchDTO;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.adregamdi.member.domain.QMember.member;
import static com.adregamdi.place.domain.QPlace.place;
import static com.adregamdi.place.domain.QPlaceReview.placeReview;
import static com.adregamdi.place.domain.QPlaceReviewImage.placeReviewImage;
import static com.adregamdi.shorts.domain.QShorts.shorts;
import static com.adregamdi.travelogue.domain.QTravelogue.travelogue;
import static com.adregamdi.travelogue.domain.QTravelogueImage.travelogueImage;

@Repository
@RequiredArgsConstructor
public class SearchRepositoryImpl implements SearchRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<TravelogueSearchDTO> searchTravelogues(String keyword, int page, int pageSize) {
        List<Tuple> results = queryFactory
                .select(
                        travelogue.travelogueId,
                        travelogue.title,
                        member.handle
                )
                .from(travelogue)
                .join(member).on(travelogue.memberId.eq(String.valueOf(member.memberId)))
                .where(travelogue.title.containsIgnoreCase(keyword))
                .offset((long) page * pageSize)
                .limit(pageSize)
                .fetch();

        Map<Long, List<String>> imageUrlMap = queryFactory
                .select(travelogueImage.travelogueId, travelogueImage.url)
                .from(travelogueImage)
                .where(travelogueImage.travelogueId.in(
                        results.stream().map(tuple -> tuple.get(travelogue.travelogueId)).collect(Collectors.toList())
                ))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> Optional.ofNullable(tuple.get(travelogueImage.travelogueId)).orElse(0L),
                        Collectors.mapping(
                                tuple -> Optional.ofNullable(tuple.get(travelogueImage.url)).orElse(""),
                                Collectors.toList()
                        )
                ));

        return results.stream()
                .map(tuple -> new TravelogueSearchDTO(
                        tuple.get(travelogue.travelogueId),
                        tuple.get(travelogue.title),
                        tuple.get(member.handle),
                        imageUrlMap.getOrDefault(tuple.get(travelogue.travelogueId), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ShortsSearchDTO> searchShorts(String keyword, int page, int pageSize) {
        return queryFactory
                .select(Projections.constructor(ShortsSearchDTO.class,
                        shorts.id,
                        shorts.title,
                        shorts.thumbnailUrl
                ))
                .from(shorts)
                .where(shorts.title.containsIgnoreCase(keyword))
                .offset((long) page * pageSize)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<PlaceSearchDTO> searchPlaces(String keyword, int page, int pageSize) {
        List<Tuple> results = queryFactory
                .select(
                        place.placeId,
                        place.title,
                        place.contentsLabel,
                        JPAExpressions.select(placeReview.count()).from(placeReview)
                                .where(placeReview.placeId.eq(place.placeId)),
                        JPAExpressions.select(shorts.count()).from(shorts)
                                .where(shorts.placeNo.eq(place.placeId))
                )
                .from(place)
                .where(place.title.containsIgnoreCase(keyword))
                .offset((long) page * pageSize)
                .limit(pageSize)
                .fetch();

        Map<Long, List<String>> imageUrlMap = queryFactory
                .select(placeReview.placeId, placeReviewImage.url)
                .from(placeReviewImage)
                .join(placeReview).on(placeReviewImage.placeReviewId.eq(placeReview.placeReviewId))
                .where(placeReview.placeId.in(
                        results.stream().map(tuple -> tuple.get(place.placeId)).collect(Collectors.toList())
                ))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> Optional.ofNullable(tuple.get(placeReview.placeId)).orElse(0L),
                        Collectors.mapping(
                                tuple -> Optional.ofNullable(tuple.get(placeReviewImage.url)).orElse(""),
                                Collectors.toList()
                        )
                ));

        return results.stream()
                .map(tuple -> new PlaceSearchDTO(
                        tuple.get(place.placeId),
                        tuple.get(place.title),
                        tuple.get(place.contentsLabel),
                        imageUrlMap.getOrDefault(tuple.get(place.placeId), Collections.emptyList()),
                        tuple.get(3, Long.class),
                        tuple.get(4, Long.class)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public long countTravelogues(String keyword) {
        return Optional.ofNullable(queryFactory
                .select(travelogue.count())
                .from(travelogue)
                .where(travelogue.title.containsIgnoreCase(keyword))
                .fetchOne()).orElse(0L);
    }

    @Override
    public long countShorts(String keyword) {
        return Optional.ofNullable(queryFactory
                .select(shorts.count())
                .from(shorts)
                .where(shorts.title.containsIgnoreCase(keyword))
                .fetchOne()).orElse(0L);
    }

    @Override
    public long countPlaces(String keyword) {
        return Optional.ofNullable(queryFactory
                .select(place.count())
                .from(place)
                .where(place.title.containsIgnoreCase(keyword))
                .fetchOne()).orElse(0L);
    }
}
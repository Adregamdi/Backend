package com.adregamdi.search.infrastructure;

import com.adregamdi.search.dto.SearchItemDTO;
import com.adregamdi.search.dto.SearchType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.adregamdi.place.domain.QPlace.place;
import static com.adregamdi.shorts.domain.QShorts.shorts;
import static com.adregamdi.travelogue.domain.QTravelogue.travelogue;

@Repository
@RequiredArgsConstructor
public class SearchRepositoryImpl implements SearchRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<SearchItemDTO> search(final String keyword, final int page, final int pageSize, final Set<SearchType> types) {
        List<SearchItemDTO> results = new ArrayList<>();

        if (types.contains(SearchType.TRAVELOGUE)) {
            results.addAll(searchTravelogues(keyword, page, pageSize));
        }
        if (types.contains(SearchType.SHORTS)) {
            results.addAll(searchShorts(keyword, page, pageSize));
        }
        if (types.contains(SearchType.PLACE)) {
            results.addAll(searchPlaces(keyword, page, pageSize));
        }

        return results.stream()
                .skip((long) page * pageSize)
                .limit(pageSize)
                .toList();
    }

    private List<SearchItemDTO> searchTravelogues(String keyword, int page, int pageSize) {
        return queryFactory
                .select(Projections.constructor(SearchItemDTO.class,
                        travelogue.travelogueId,
                        travelogue.title,
                        Expressions.constant(SearchType.TRAVELOGUE),
                        travelogue.introduction))
                .from(travelogue)
                .where(keywordCondition(keyword, travelogue.title))
                .offset((long) page * pageSize)
                .limit(pageSize)
                .fetch();
    }

    private List<SearchItemDTO> searchShorts(String keyword, int page, int pageSize) {
        return queryFactory
                .select(Projections.constructor(SearchItemDTO.class,
                        shorts.id,
                        shorts.title,
                        Expressions.constant(SearchType.SHORTS),
                        shorts.thumbnailUrl))
                .from(shorts)
                .where(keywordCondition(keyword, shorts.title))
                .offset((long) page * pageSize)
                .limit(pageSize)
                .fetch();
    }

    private List<SearchItemDTO> searchPlaces(String keyword, int page, int pageSize) {
        return queryFactory
                .select(Projections.constructor(SearchItemDTO.class,
                        place.placeId,
                        place.title,
                        Expressions.constant(SearchType.PLACE),
                        place.introduction))
                .from(place)
                .where(keywordCondition(keyword, place.title))
                .offset((long) page * pageSize)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public long countTotal(final String keyword) {
        Long travelogueCount = Optional.ofNullable(queryFactory
                .select(travelogue.count())
                .from(travelogue)
                .where(keywordCondition(keyword, travelogue.title))
                .fetchOne()).orElse(0L);

        Long shortsCount = Optional.ofNullable(queryFactory
                .select(shorts.count())
                .from(shorts)
                .where(keywordCondition(keyword, shorts.title))
                .fetchOne()).orElse(0L);

        Long placeCount = Optional.ofNullable(queryFactory
                .select(place.count())
                .from(place)
                .where(keywordCondition(keyword, place.title))
                .fetchOne()).orElse(0L);

        return travelogueCount + shortsCount + placeCount;
    }

    private BooleanExpression keywordCondition(String keyword, StringPath field) {
        return keyword != null && !keyword.isEmpty() ? field.containsIgnoreCase(keyword) : null;
    }
}
package com.adregamdi.place.infrastructure;

import com.adregamdi.place.domain.Place;
import com.adregamdi.place.dto.PopularPlaceDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.adregamdi.core.utils.RepositoryUtil.makeOrderSpecifiers;
import static com.adregamdi.place.domain.QPlace.place;
import static com.adregamdi.place.domain.QPlaceReview.placeReview;
import static com.adregamdi.shorts.domain.QShorts.shorts;

@Repository
@RequiredArgsConstructor
public class PlaceCustomRepositoryImpl implements PlaceCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Slice<Place>> findByTitleStartingWith(final String title, final Pageable pageable) {
        List<Place> places = jpaQueryFactory
                .selectFrom(place)
                .where(place.title.startsWith(title))
                .orderBy(makeOrderSpecifiers(place, pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return Optional.of(new SliceImpl<>(places));
    }


    @Override
    public List<PopularPlaceDTO> findInOrderOfPopularAddCount(final Long lastId, final Integer lastAddCount) {
        BooleanExpression whereCondition = ltAddCountAndGtPlaceId(lastAddCount, lastId);

        return jpaQueryFactory
                .select(Projections.constructor(PopularPlaceDTO.class,
                        place,
                        JPAExpressions.select(placeReview.count())
                                .from(placeReview)
                                .where(placeReview.placeId.eq(place.placeId)),
                        JPAExpressions.select(shorts.count())
                                .from(shorts)
                                .where(shorts.placeId.eq(place.placeId))))
                .from(place)
                .where(whereCondition)
                .orderBy(place.addCount.desc(), place.placeId.asc())
                .limit(10)
                .fetch();
    }

    private BooleanExpression ltAddCountAndGtPlaceId(Integer lastAddCount, Long lastId) {
        if (lastAddCount == null || lastId == null) {
            return null;
        }
        return place.addCount.lt(lastAddCount)
                .or(place.addCount.eq(lastAddCount).and(place.placeId.gt(lastId)));
    }
}

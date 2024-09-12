package com.adregamdi.place.infrastructure;

import com.adregamdi.place.domain.Place;
import com.adregamdi.place.dto.PopularPlaceDTO;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.adregamdi.core.utils.RepositoryUtil.makeOrderSpecifiers;
import static com.adregamdi.place.domain.QPlace.place;
import static com.adregamdi.place.domain.QPlaceReview.placeReview;
import static com.adregamdi.place.domain.QPlaceReviewImage.placeReviewImage;
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

        List<Tuple> results = jpaQueryFactory
                .select(place,
                        placeReview.count(),
                        shorts.count(),
                        Expressions.stringTemplate(
                                "group_concat({0})",
                                placeReviewImage.url).as("imageUrls"))
                .from(place)
                .leftJoin(placeReview).on(placeReview.placeId.eq(place.placeId))
                .leftJoin(shorts).on(shorts.placeId.eq(place.placeId))
                .leftJoin(placeReviewImage).on(placeReviewImage.placeReviewId.eq(placeReview.placeReviewId))
                .where(whereCondition)
                .groupBy(place.placeId)
                .orderBy(place.addCount.desc(), place.placeId.asc())
                .limit(11)
                .fetch();

        return results.stream()
                .map(tuple -> new PopularPlaceDTO(
                        tuple.get(place),
                        tuple.get(placeReview.count()),
                        tuple.get(shorts.count()),
                        parseImageUrls(tuple.get(3, String.class))
                ))
                .collect(Collectors.toList());
    }

    private List<String> parseImageUrls(String imageUrlString) {
        if (imageUrlString == null || imageUrlString.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(imageUrlString.split(","));
    }

    private BooleanExpression ltAddCountAndGtPlaceId(Integer lastAddCount, Long lastId) {
        if (lastAddCount == null || lastId == null) {
            return null;
        }
        return place.addCount.lt(lastAddCount)
                .or(place.addCount.eq(lastAddCount).and(place.placeId.gt(lastId)));
    }
}

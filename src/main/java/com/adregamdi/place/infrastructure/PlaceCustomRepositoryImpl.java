package com.adregamdi.place.infrastructure;

import com.adregamdi.place.domain.Place;
import com.adregamdi.place.dto.PopularPlaceDTO;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.*;

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
                        placeReview.count().as("reviewCount"),
                        shorts.count().as("shortsCount"),
                        placeReviewImage.url,
                        placeReviewImage.placeReviewImageId)
                .from(place)
                .leftJoin(placeReview).on(placeReview.placeId.eq(place.placeId))
                .leftJoin(shorts).on(shorts.placeId.eq(place.placeId))
                .leftJoin(placeReviewImage).on(placeReviewImage.placeReviewId.eq(placeReview.placeReviewId))
                .where(whereCondition)
                .groupBy(place.placeId, placeReviewImage.url, placeReviewImage.placeReviewImageId)
                .orderBy(place.addCount.desc(), place.placeId.asc(), placeReviewImage.placeReviewImageId.desc())
                .limit(11)
                .fetch();

        Map<Long, PopularPlaceDTO> dtoMap = new LinkedHashMap<>();

        for (Tuple tuple : results) {
            Place placeEntity = tuple.get(place);
            Long placeId = placeEntity.getPlaceId();
            Long photoReviewCount = tuple.get(1, Long.class);
            Long shortsCount = tuple.get(2, Long.class);
            String imageUrl = tuple.get(placeReviewImage.url);

            PopularPlaceDTO dto = dtoMap.computeIfAbsent(placeId, k ->
                    new PopularPlaceDTO(placeEntity, photoReviewCount, shortsCount, new ArrayList<>()));

            if (imageUrl != null && dto.imageUrls().size() < 5) {
                dto.imageUrls().add(imageUrl);
            }
        }

        for (PopularPlaceDTO dto : dtoMap.values()) {
            if (dto.imageUrls().size() < 5 && dto.place().getImgPath() != null && !dto.place().getImgPath().isEmpty()) {
                dto.imageUrls().add(dto.place().getImgPath());
            }
        }

        return new ArrayList<>(dtoMap.values());
    }

    private BooleanExpression ltAddCountAndGtPlaceId(Integer lastAddCount, Long lastId) {
        if (lastAddCount == null || lastId == null) {
            return null;
        }
        return place.addCount.lt(lastAddCount)
                .or(place.addCount.eq(lastAddCount).and(place.placeId.gt(lastId)));
    }
}

package com.adregamdi.place.infrastructure;

import com.adregamdi.place.domain.Place;
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

@Repository
@RequiredArgsConstructor
public class PlaceCustomRepositoryImpl implements PlaceCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Slice<Place>> findByNameStartingWith(Pageable pageable, String title) {
        List<Place> places = jpaQueryFactory
                .selectFrom(place)
                .where(place.title.startsWith(title))
                .orderBy(makeOrderSpecifiers(place, pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return Optional.of(new SliceImpl<>(places));
    }
}

package com.adregamdi.travel.infrastructure;

import com.adregamdi.travel.dto.TravelDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.adregamdi.core.utils.RepositoryUtil.makeOrderSpecifiers;
import static com.adregamdi.travel.domain.QTravel.travel;


@RequiredArgsConstructor
@Repository
public class TravelCustomRepositoryImpl implements TravelCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<TravelDTO> findByMemberId(
            final String memberId,
            final Pageable pageable
    ) {
        List<TravelDTO> results = jpaQueryFactory
                .select(Projections.constructor(TravelDTO.class,
                        travel.startDate,
                        travel.endDate,
                        travel.title))
                .from(travel)
                .where(travel.memberId.eq(UUID.fromString(memberId)))
                .orderBy(makeOrderSpecifiers(travel, pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        List<TravelDTO> content = hasNext ? results.subList(0, pageable.getPageSize()) : results;


        return new SliceImpl<>(content, pageable, hasNext);
    }
}

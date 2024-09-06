package com.adregamdi.travelogue.infrastructure;

import com.adregamdi.travelogue.dto.TravelogueDTO;
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
import static com.adregamdi.travelogue.domain.QTravelogue.travelogue;

@RequiredArgsConstructor
@Repository
public class TravelogueCustomRepositoryImpl implements TravelogueCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<TravelogueDTO> findByMemberId(final String memberId, final Pageable pageable) {
        List<TravelogueDTO> results = jpaQueryFactory
                .select(Projections.constructor(TravelogueDTO.class,
                        travelogue.travelogueId,
                        travelogue.title))
                .from(travelogue)
                .where(travelogue.memberId.eq(UUID.fromString(memberId)))
                .orderBy(makeOrderSpecifiers(travelogue, pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        List<TravelogueDTO> content = hasNext ? results.subList(0, pageable.getPageSize()) : results;

        return new SliceImpl<>(content, pageable, hasNext);
    }
}

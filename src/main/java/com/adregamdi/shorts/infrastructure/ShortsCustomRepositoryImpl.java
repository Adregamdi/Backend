package com.adregamdi.shorts.infrastructure;

import com.adregamdi.shorts.dto.ShortsDTO;
import com.adregamdi.shorts.dto.response.GetShortsResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.adregamdi.shorts.domain.QShorts.shorts;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ShortsCustomRepositoryImpl implements ShortsCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public GetShortsResponse getShortsForMember(String memberId, long lastId, int size) {

        List<ShortsDTO> content = jpaQueryFactory
                .select(Projections.constructor(ShortsDTO.class,
                        shorts.id,
                        shorts.title,
                        shorts.memberId,
                        shorts.placeNo,
                        shorts.travelReviewNo))
                .from(shorts)
                .where(shorts.id.gt(lastId))
                .orderBy(shorts.id.asc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = content.size() > size;
        if (hasNext) {
            content.remove(size);
        }

        return new GetShortsResponse(content, hasNext);
    }

}

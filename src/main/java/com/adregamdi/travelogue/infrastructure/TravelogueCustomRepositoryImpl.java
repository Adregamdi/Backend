package com.adregamdi.travelogue.infrastructure;

import com.adregamdi.travelogue.dto.TravelogueDTO;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.adregamdi.core.utils.RepositoryUtil.makeOrderSpecifiers;
import static com.adregamdi.member.domain.QMember.member;
import static com.adregamdi.travelogue.domain.QTravelogue.travelogue;
import static com.adregamdi.travelogue.domain.QTravelogueImage.travelogueImage;

@RequiredArgsConstructor
@Repository
public class TravelogueCustomRepositoryImpl implements TravelogueCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<TravelogueDTO> findByMemberId(final String memberId, final Pageable pageable) {
        List<Tuple> results = jpaQueryFactory
                .select(travelogue.travelogueId,
                        travelogue.title,
                        member.handle,
                        travelogueImage.url)
                .from(travelogue)
                .join(member).on(travelogue.memberId.eq(member.memberId))
                .leftJoin(travelogueImage).on(travelogue.travelogueId.eq(travelogueImage.travelogueId))
                .where(travelogue.memberId.eq(memberId))
                .orderBy(makeOrderSpecifiers(travelogue, pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        Map<Long, TravelogueDTO> dtoMap = new LinkedHashMap<>();
        for (Tuple row : results) {
            Long travelogueId = row.get(travelogue.travelogueId);
            TravelogueDTO dto = dtoMap.computeIfAbsent(travelogueId,
                    id -> new TravelogueDTO(
                            id,
                            row.get(travelogue.title),
                            row.get(member.handle),
                            new ArrayList<>()
                    )
            );

            String imageUrl = row.get(travelogueImage.url);
            if (imageUrl != null) {
                dto.imageUrls().add(imageUrl);
            }
        }

        List<TravelogueDTO> content = new ArrayList<>(dtoMap.values());
        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content = content.subList(0, pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<TravelogueDTO> findOrderByCreatedAt(final Pageable pageable) {
        List<Tuple> results = jpaQueryFactory
                .select(travelogue.travelogueId,
                        travelogue.title,
                        member.handle,
                        travelogueImage.url)
                .from(travelogue)
                .join(member).on(travelogue.memberId.eq(member.memberId))
                .leftJoin(travelogueImage).on(travelogue.travelogueId.eq(travelogueImage.travelogueId))
                .orderBy(makeOrderSpecifiers(travelogue, pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        Map<Long, TravelogueDTO> dtoMap = new LinkedHashMap<>();
        for (Tuple row : results) {
            Long travelogueId = row.get(travelogue.travelogueId);
            TravelogueDTO dto = dtoMap.computeIfAbsent(travelogueId,
                    id -> new TravelogueDTO(
                            id,
                            row.get(travelogue.title),
                            row.get(member.handle),
                            new ArrayList<>()
                    )
            );

            String imageUrl = row.get(travelogueImage.url);
            if (imageUrl != null) {
                dto.imageUrls().add(imageUrl);
            }
        }

        List<TravelogueDTO> content = new ArrayList<>(dtoMap.values());
        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content = content.subList(0, pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}

package com.adregamdi.travelogue.infrastructure;

import com.adregamdi.like.domain.enumtype.ContentType;
import com.adregamdi.travelogue.dto.HotTravelogueDTO;
import com.adregamdi.travelogue.dto.TravelogueDTO;
import com.adregamdi.travelogue.dto.response.GetHotTraveloguesResponse;
import com.adregamdi.travelogue.dto.response.GetMemberTraveloguesResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static com.adregamdi.block.domain.QBlock.block;
import static com.adregamdi.core.utils.RepositoryUtil.makeOrderSpecifiers;
import static com.adregamdi.like.domain.QLike.like;
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
                        member.memberId,
                        member.profile,
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
                            row.get(member.memberId),
                            row.get(member.profile),
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
    public Slice<TravelogueDTO> findOrderByCreatedAt(final String memberId, final Pageable pageable) {
        List<Tuple> results = jpaQueryFactory
                .select(travelogue.travelogueId,
                        travelogue.title,
                        member.memberId,
                        member.profile,
                        member.handle,
                        travelogueImage.url)
                .from(travelogue)
                .join(member).on(travelogue.memberId.eq(member.memberId))
                .leftJoin(travelogueImage).on(travelogue.travelogueId.eq(travelogueImage.travelogueId))
                .leftJoin(block).on(block.blockingMemberId.eq(memberId)
                        .and(block.blockedMemberId.eq(member.memberId)))
                .where(block.blockId.isNull())
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
                            row.get(member.memberId),
                            row.get(member.profile),
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
    public GetHotTraveloguesResponse findOrderByLikeCount(String memberId, int lastLikeCount, int size) {

        NumberExpression<Integer> likeCountExpression = like.likeId.countDistinct().intValue();
        BooleanExpression havingCondition = lastLikeCount == -1 ? null : likeCountExpression.loe(lastLikeCount);

        List<Tuple> results = jpaQueryFactory
                .select(
                        travelogue.travelogueId,
                        travelogue.title,
                        travelogue.memberId,
                        member.handle,
                        member.profile,
                        likeCountExpression.as("likeCount")
                )
                .from(travelogue)
                .leftJoin(member).on(travelogue.memberId.eq(member.memberId))
                .leftJoin(like).on(like.contentId.eq(travelogue.travelogueId)
                        .and(like.contentType.eq(ContentType.TRAVELOGUE)))
                .leftJoin(block).on(block.blockingMemberId.eq(memberId)
                        .and(block.blockedMemberId.eq(member.memberId)))
                .where(block.blockId.isNull())
                .groupBy(travelogue.travelogueId, travelogue.title, travelogue.memberId, member.handle, member.profile)
                .having(havingCondition)
                .orderBy(likeCountExpression.desc(), travelogue.travelogueId.desc())
                .limit(size + 1)
                .fetch();

        List<Long> travelogueIds = results.stream()
                .map(tuple -> tuple.get(travelogue.travelogueId))
                .collect(Collectors.toList());

        Map<Long, List<String>> imageMap = jpaQueryFactory
                .select(travelogueImage.travelogueId, travelogueImage.url)
                .from(travelogueImage)
                .where(travelogueImage.travelogueId.in(travelogueIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(travelogueImage.travelogueId),
                        Collectors.mapping(tuple -> tuple.get(travelogueImage.url), Collectors.toList())
                ));

        List<HotTravelogueDTO> contents = results.stream()
                .map(tuple -> new HotTravelogueDTO(
                        tuple.get(travelogue.travelogueId),
                        tuple.get(travelogue.title),
                        tuple.get(travelogue.memberId),
                        tuple.get(member.handle),
                        tuple.get(member.profile),
                        tuple.get(5, Integer.class),
                        imageMap.getOrDefault(tuple.get(travelogue.travelogueId), Collections.emptyList())
                ))
                .collect(Collectors.toList());

        boolean hasNext = contents.size() > size;
        if (hasNext) {
            contents = contents.subList(0, size);
        }

        return new GetHotTraveloguesResponse(hasNext, contents);
    }

    @Override
    public GetMemberTraveloguesResponse findMemberTravelogues(String memberId, Long lastTravelogueId, int size) {

        List<Tuple> results = jpaQueryFactory
                .select(
                        travelogue.travelogueId,
                        travelogue.title,
                        member.memberId,
                        member.profile,
                        member.handle,
                        travelogueImage.url,
                        travelogue.createdAt
                )
                .from(travelogue)
                .join(member).on(travelogue.memberId.eq(member.memberId))
                .leftJoin(travelogueImage).on(travelogue.travelogueId.eq(travelogueImage.travelogueId))
                .where(
                        travelogue.memberId.eq(memberId),
                        travelogue.travelogueId.lt(lastTravelogueId)
                )
                .orderBy(travelogue.travelogueId.desc())
                .limit(size + 1)
                .fetch();

        // 결과 처리
        Map<Long, TravelogueDTO> dtoMap = new LinkedHashMap<>();
        for (Tuple row : results) {
            Long travelogueId = row.get(travelogue.travelogueId);
            TravelogueDTO dto = dtoMap.computeIfAbsent(travelogueId,
                    id -> new TravelogueDTO(
                            id,
                            row.get(travelogue.title),
                            row.get(member.memberId),
                            row.get(member.profile),
                            row.get(member.handle),
                            new ArrayList<>()
                    )
            );

            String imageUrl = row.get(travelogueImage.url);
            if (imageUrl != null && !dto.imageUrls().contains(imageUrl)) {
                dto.imageUrls().add(imageUrl);
            }
        }

        List<TravelogueDTO> travelogueList = new ArrayList<>(dtoMap.values());
        boolean hasNext = travelogueList.size() > size;
        if (hasNext) {
            travelogueList = travelogueList.subList(0, size);
        }

        return new GetMemberTraveloguesResponse(hasNext, travelogueList);
    }
}

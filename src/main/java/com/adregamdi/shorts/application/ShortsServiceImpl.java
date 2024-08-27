package com.adregamdi.shorts.application;

import com.adregamdi.media.application.MediaService;
import com.adregamdi.media.enumtype.MediaType;
import com.adregamdi.shorts.domain.Shorts;
import com.adregamdi.shorts.dto.request.CreateShortsRequest;
import com.adregamdi.shorts.dto.request.UpdateShortsRequest;
import com.adregamdi.shorts.dto.response.GetShortsResponse;
import com.adregamdi.shorts.exception.ShortsException;
import com.adregamdi.shorts.infrastructure.ShortsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.adregamdi.core.constant.Constant.LARGE_PAGE_SIZE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ShortsServiceImpl implements ShortsService{

    private final ShortsRepository shortsRepository;
    private final MediaService mediaService;

    private final ShortsValidService shortsValidService;

    @Override
    public GetShortsResponse getShorts(String memberId, long lastId) {

        GetShortsResponse shortsDTOList = shortsRepository.getShortsForMember(memberId, lastId, LARGE_PAGE_SIZE);
        return shortsDTOList;
    }

    @Override
    public void saveShorts(final String memberId, final CreateShortsRequest request) {

        Shorts savedShorts = shortsRepository.save(
                Shorts.builder()
                        .title(request.title())
                        .memberId(UUID.fromString(memberId))
                        .placeNo(request.placeNo())
                        .travelReviewNo(request.travelReviewNo())
                        .build()
        );

        mediaService.assignVideo(request.videoUrl(), savedShorts.getId(), MediaType.VIDEO);
        mediaService.assignVideo(request.thumbnailUrl(), savedShorts.getId(), MediaType.THUMBNAIL);

        log.info("Shorts saved. shortsId: {}", savedShorts.getId());
    }


    @Override
    public void updateShorts(String memberId, UpdateShortsRequest request) {

        Shorts savedShorts = shortsRepository.findById(request.shortsId())
                .orElseThrow(() -> new ShortsException.ShortsNotFoundException(request.shortsId()));

        if (!shortsValidService.isWriter(memberId, savedShorts.getMemberId())) {
            log.warn("Invalid Member Request. memberId: {}", memberId);
            throw new ShortsException.ShortsNOTWRITERException(memberId);
        }

        savedShorts.update(request);
        log.info("Shorts updated. shortsId: {}", savedShorts.getId());
    }
}

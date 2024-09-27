package com.adregamdi.block.application;

import com.adregamdi.block.domain.Block;
import com.adregamdi.block.dto.request.CreateBlockRequest;
import com.adregamdi.block.dto.request.DeleteBlockRequest;
import com.adregamdi.block.dto.response.GetMyBlockingMembers;
import com.adregamdi.block.exception.BlockException;
import com.adregamdi.block.infrastructure.BlockRepository;
import com.adregamdi.member.exception.MemberException;
import com.adregamdi.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BlockService {
    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;

    /*
     * 차단하기
     * */
    @Transactional
    public void create(final String memberId, final CreateBlockRequest request) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException.MemberNotFoundException(memberId));

        memberRepository.findById(request.blockedMemberId())
                .orElseThrow(() -> new MemberException.MemberNotFoundException(request.blockedMemberId()));

        blockRepository.findByBlockedMemberIdAndBlockingMemberId(request.blockedMemberId(), memberId)
                .orElseThrow(BlockException.BlockExistException::new);

        blockRepository.save(Block.builder()
                .blockedMemberId(request.blockedMemberId())
                .blockingMemberId(memberId)
                .build());
    }

    /*
     * 내 차단 목록 조회
     * */
    @Transactional(readOnly = true)
    public GetMyBlockingMembers getMyBlockingMembers(final String memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException.MemberNotFoundException(memberId));

        List<Block> blocks = blockRepository.findByBlockingMemberId(memberId);

        return GetMyBlockingMembers.from(blocks);
    }

    /*
     * 차단해제
     * */
    @Transactional
    public void delete(final String memberId, final DeleteBlockRequest request) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException.MemberNotFoundException(memberId));

        memberRepository.findById(request.blockedMemberId())
                .orElseThrow(() -> new MemberException.MemberNotFoundException(request.blockedMemberId()));

        blockRepository.findByBlockedMemberIdAndBlockingMemberId(request.blockedMemberId(), memberId)
                .orElseThrow(BlockException.BlockNotFoundException::new);

        blockRepository.delete(Block.builder()
                .blockedMemberId(request.blockedMemberId())
                .blockingMemberId(memberId)
                .build());
    }
}
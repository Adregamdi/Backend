package com.adregamdi.block.application;

import com.adregamdi.block.domain.Block;
import com.adregamdi.block.dto.BlockDTO;
import com.adregamdi.block.dto.response.CreateBlockResponse;
import com.adregamdi.block.dto.response.GetMyBlockingMembers;
import com.adregamdi.block.exception.BlockException;
import com.adregamdi.block.infrastructure.BlockRepository;
import com.adregamdi.member.domain.Member;
import com.adregamdi.member.exception.MemberException;
import com.adregamdi.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BlockServiceImpl implements BlockService {
    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;

    /*
     * [차단하기]
     * */
    @Override
    @Transactional
    public CreateBlockResponse create(final String memberId, final String blockedMemberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException.MemberNotFoundException(memberId));

        memberRepository.findById(blockedMemberId)
                .orElseThrow(() -> new MemberException.MemberNotFoundException(blockedMemberId));

        Block block = blockRepository.findByBlockedMemberIdAndBlockingMemberId(blockedMemberId, memberId)
                .orElse(blockRepository.save(Block.builder()
                        .blockedMemberId(blockedMemberId)
                        .blockingMemberId(memberId)
                        .build()));

        return new CreateBlockResponse(block);
    }

    /*
     * [내 차단 목록 조회]
     * */
    @Override
    @Transactional(readOnly = true)
    public GetMyBlockingMembers getMyBlockingMembers(final String memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException.MemberNotFoundException(memberId));

        List<Block> blocks = blockRepository.findByBlockingMemberId(memberId);

        List<BlockDTO> blockInfos = new ArrayList<>();
        if (!blocks.isEmpty()) {
            for (Block block : blocks) {
                Optional<Member> blockedMember = memberRepository.findById(block.getBlockedMemberId());
                if (blockedMember.isEmpty()) {
                    continue;
                }

                blockInfos.add(BlockDTO.builder()
                        .blockId(block.getBlockId())
                        .blockedMemberId(blockedMember.get().getMemberId())
                        .blockedMemberName(blockedMember.get().getName())
                        .blockedMemberProfile(blockedMember.get().getProfile())
                        .blockedMemberHandle(blockedMember.get().getHandle())
                        .build());
            }
        }

        return GetMyBlockingMembers.from(blockInfos);
    }

    /*
     * [차단해제]
     * */
    @Override
    @Transactional
    public void delete(final String memberId, final String blockedMemberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException.MemberNotFoundException(memberId));

        memberRepository.findById(blockedMemberId)
                .orElseThrow(() -> new MemberException.MemberNotFoundException(blockedMemberId));

        Block block = blockRepository.findByBlockedMemberIdAndBlockingMemberId(blockedMemberId, memberId)
                .orElseThrow(BlockException.BlockNotFoundException::new);

        blockRepository.delete(block);
    }
}

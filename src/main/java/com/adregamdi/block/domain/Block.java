package com.adregamdi.block.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tbl_block")
public class Block extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blockId;
    @Column
    private String blockedMemberId;
    @Column
    private String blockingMemberId;

    @Builder
    public Block(String blockedMemberId, String blockingMemberId) {
        this.blockedMemberId = blockedMemberId;
        this.blockingMemberId = blockingMemberId;
    }
}

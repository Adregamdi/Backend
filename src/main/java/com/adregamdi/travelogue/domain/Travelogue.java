package com.adregamdi.travelogue.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_travelogue")
public class Travelogue extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelogueId;
    @Column
    private String memberId; // 회원 id
    @Column
    private Long travelId; // 일정 id
    @Column
    private String title; // 제목
    @Column
    private String introduction; // 소개

    public Travelogue(String memberId, Long travelId, String title, String introduction) {
        this.memberId = memberId;
        this.travelId = travelId;
        this.title = title;
        this.introduction = introduction;
    }
}

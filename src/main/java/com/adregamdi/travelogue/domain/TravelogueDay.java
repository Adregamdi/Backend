package com.adregamdi.travelogue.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_travelogue_day")
public class TravelogueDay extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelogueDayId;
    @Column
    private Long travelogueId; // 여행기 id
    @Column
    private Integer day; // 해당 날짜
    @Column
    private String content; // 내용
}

package com.adregamdi.travelogue.domain;

import com.adregamdi.core.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tbl_travelogue_day")
public class TravelogueDay extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelogueDayId;
    @Column
    private Long travelogueId; // 여행기 id
    @Column
    private LocalDate date; // 해당 날짜
    @Column
    private Integer day; // 해당 날
    @Column
    private String content; // 내용

    @Builder
    public TravelogueDay(Long travelogueId, LocalDate date, Integer day, String content) {
        this.travelogueId = travelogueId;
        this.date = date;
        this.day = day;
        this.content = content;
    }
}

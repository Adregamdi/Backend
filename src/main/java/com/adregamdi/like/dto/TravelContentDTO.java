package com.adregamdi.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TravelContentDTO {

    private Long scheduleId; // 일정 ID
    private String memberId; // 회원 ID
    private LocalDate startDate; // 시작일
    private LocalDate endDate; // 종료일
    private String title; // 제목
    private Integer day; // 해당 날짜
    private String memo; // 메모
}
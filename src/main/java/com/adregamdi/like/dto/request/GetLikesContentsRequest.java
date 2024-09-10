package com.adregamdi.like.dto.request;

import com.adregamdi.like.domain.enumtype.SelectedType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record GetLikesContentsRequest(
        @Pattern(regexp = "(?i)ALL|SHORTS|PLACE|TRAVEL", message = "컨텐츠 종류가 설정되어야 합니다.")
        String select,
        @NotBlank(message = "요청에 회원 정보가 포함되어야 합니다.")
        String memberId,
        @PositiveOrZero(message = "마지막 쇼츠 식별 값이 필요합니다.")
        Long lastLikeId,
        @Positive
        int size
) {

    public SelectedType getSelectedType() {
        if (select.isBlank()) {
            return SelectedType.ALL; // 또는 다른 기본값
        }
        try {
            return SelectedType.valueOf(select.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SelectedType.ALL; // 또는 예외 처리
        }
    }

}
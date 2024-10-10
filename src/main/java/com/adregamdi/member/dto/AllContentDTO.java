package com.adregamdi.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AllContentDTO {

    private String contentType;
    private Long contentId;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private String detailUri;

    public AllContentDTO(String contentType, Long contentId, String thumbnailUrl, LocalDateTime createdAt) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.thumbnailUrl = thumbnailUrl;
        this.createdAt = createdAt;
        this.detailUri = generateDetailUri(contentType, contentId);
    }
    
    private String generateDetailUri(String contentType, Long contentId) {
        return switch (contentType.toUpperCase()) {
            case "TRAVELOGUE" -> "/api/travelogue?travelogue_id=" + contentId;
            case "SHORTS" -> "/api/shorts/stream/" + contentId;
            case "PLACE_REVIEW" -> "/api/place?place_id=" + contentId;
            default -> throw new IllegalStateException("유효하지 않은 값: " + contentType.toUpperCase());
        };
    }
}

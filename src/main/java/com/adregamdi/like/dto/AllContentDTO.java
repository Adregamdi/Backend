package com.adregamdi.like.dto;

import com.adregamdi.like.domain.enumtype.ContentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AllContentDTO{

    private String title;
    private ContentType contentType;
    private Long contentId;
    private String thumbnailUrl;
    private String detailUri;

    public AllContentDTO(String title, ContentType contentType, Long contentId, String thumbnailUrl) {
        this.title = title;
        this.contentType = contentType;
        this.contentId = contentId;
        this.thumbnailUrl = thumbnailUrl;
        this.detailUri = generateDetailUri(contentType, contentId);
    }

    private String generateDetailUri(ContentType contentType, Long contentId) {
        return switch (contentType) {
            case SHORTS -> "/api/shorts/stream/" + contentId;
            case PLACE -> "/api/places?place_id=" + contentId;
            case TRAVELOGUE -> "/api/travelogue?travelogue_id=" + contentId;
        };
    }

}
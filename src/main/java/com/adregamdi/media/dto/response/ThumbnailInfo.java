package com.adregamdi.media.dto.response;

import com.adregamdi.media.enumtype.MediaType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ThumbnailInfo {

    private MediaType mediaType;
    private String url;
}

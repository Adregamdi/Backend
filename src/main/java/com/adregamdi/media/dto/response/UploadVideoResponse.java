package com.adregamdi.media.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadVideoResponse {

    private String videoUrl;
    private String videoThumbnailUrl;
}

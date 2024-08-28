package com.adregamdi.shorts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadVideoDTO {

    private String videoUrl;
    private String videoThumbnailUrl;
}

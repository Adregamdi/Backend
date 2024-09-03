package com.adregamdi.media.dto.request;

import java.util.List;

public record DeleteImagesRequest(
        List<String> images
) {

}

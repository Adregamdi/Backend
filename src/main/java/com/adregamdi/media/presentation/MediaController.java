package com.adregamdi.media.presentation;

import com.adregamdi.core.handler.ApiResponse;
import com.adregamdi.media.application.MediaService;
import com.adregamdi.media.application.VideoService;
import com.adregamdi.media.dto.response.UploadVideoResponse;
import com.adregamdi.media.enumtype.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/media")
public class MediaController {

    private final VideoService videoService;
    private final MediaService mediaService;

    @PostMapping("/upload-video")
    public ResponseEntity<ApiResponse<UploadVideoResponse>> uploadVideo(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("shorts") final MultipartFile videoFile
    ) throws EncoderException, IOException {

        String memberId = userDetails.getUsername();
        UploadVideoResponse response = videoService.uploadVideo(videoFile, memberId);
        mediaService.saveVideo(response.getVideoUrl(), MediaType.VIDEO);
        mediaService.saveVideo(response.getVideoThumbnailUrl(), MediaType.THUMBNAIL);

        return ResponseEntity.ok()
                .body(ApiResponse.<UploadVideoResponse>builder()
                        .data(response)
                        .build());
    }
}

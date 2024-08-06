package com.adregamdi.shorts.application;


import org.springframework.web.multipart.MultipartFile;

public interface ShortsService {
    String uploadVideo(MultipartFile video);
}

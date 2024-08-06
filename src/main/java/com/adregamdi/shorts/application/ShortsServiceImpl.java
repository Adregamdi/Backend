package com.adregamdi.shorts.application;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ShortsServiceImpl implements ShortsService{

    @Override
    public String uploadVideo(MultipartFile video) {
        return "";
    }
}

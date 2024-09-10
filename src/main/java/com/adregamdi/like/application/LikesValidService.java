package com.adregamdi.like.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class LikesValidService {

    public boolean isWriter(String memberId, String target) {
        log.info("memberId: {}, target: {}", memberId, target);
        return Objects.equals(memberId, target);
    }
}
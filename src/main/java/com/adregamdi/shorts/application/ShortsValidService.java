package com.adregamdi.shorts.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class ShortsValidService {


    public boolean isWriter(
            final Object memberId,
            final Object savedMemberId
    ) {
        return Objects.equals( memberId, savedMemberId);
    }

}

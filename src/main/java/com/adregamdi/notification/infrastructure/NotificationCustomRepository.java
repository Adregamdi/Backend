package com.adregamdi.notification.infrastructure;

import com.adregamdi.notification.dto.NotificationPageResult;

import java.time.LocalDateTime;

public interface NotificationCustomRepository {
    NotificationPageResult findByMemberId(final String memberId, final LocalDateTime date, final Long lastId);
}

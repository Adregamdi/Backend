package com.adregamdi.notification.infrastructure;

import com.adregamdi.notification.domain.Notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationCustomRepository {
    Optional<List<Notification>> findByMemberId(final String memberId, final LocalDateTime date, final Long lastId);
}

package com.adregamdi.notification.infrastructure;

import com.adregamdi.notification.domain.Notification;
import com.adregamdi.notification.dto.NotificationPageResult;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.adregamdi.notification.domain.QNotification.notification;

@RequiredArgsConstructor
@Repository
public class NotificationCustomRepositoryImpl implements NotificationCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public NotificationPageResult findByMemberId(
            final String memberId,
            final LocalDateTime date,
            final Long lastId
    ) {
        List<Notification> notifications = jpaQueryFactory
                .selectFrom(notification)
                .where(
                        notification.memberId.eq(memberId),
                        notification.createdAt.gt(date),
                        lastIdCondition(lastId)
                )
                .orderBy(notification.notificationId.desc())
                .limit(11)
                .fetch();

        boolean hasNext = notifications.size() > 10;
        List<Notification> result = hasNext ? notifications.subList(0, 10) : notifications;

        return new NotificationPageResult(hasNext, result);
    }

    private BooleanExpression lastIdCondition(final Long lastId) {
        return lastId != null ? notification.notificationId.lt(lastId) : null;
    }
}

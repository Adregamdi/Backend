package com.adregamdi.notification.infrastructure;

import com.adregamdi.notification.domain.Notification;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.adregamdi.notification.domain.QNotification.notification;

@RequiredArgsConstructor
@Repository
public class NotificationCustomRepositoryImpl implements NotificationCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<List<Notification>> findByMemberId(
            final String memberId,
            final LocalDateTime date,
            final Long lastId
    ) {
        List<Notification> notifications = jpaQueryFactory
                .select(notification)
                .from(notification)
                .where(
                        notification.memberId.eq(memberId),
                        notification.createdAt.gt(date),
                        toContainsLastId(lastId)
                )
                .orderBy(notification.createdAt.desc())
                .limit(10)
                .fetch();

        return Optional.of(notifications);
    }

    private BooleanExpression toContainsLastId(final Long lastId) {
        if (lastId == null) {
            return null;
        }
        return notification.notificationId.lt(lastId);
    }
}

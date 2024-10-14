package com.adregamdi.notification.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotification is a Querydsl query type for Notification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotification extends EntityPathBase<Notification> {

    private static final long serialVersionUID = 655974571L;

    public static final QNotification notification = new QNotification("notification");

    public final com.adregamdi.core.entity.QBaseTime _super = new com.adregamdi.core.entity.QBaseTime(this);

    public final NumberPath<Long> contentId = createNumber("contentId", Long.class);

    public final EnumPath<com.adregamdi.core.constant.ContentType> contentType = createEnum("contentType", com.adregamdi.core.constant.ContentType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final BooleanPath isRead = createBoolean("isRead");

    public final StringPath memberId = createString("memberId");

    public final NumberPath<Long> notificationId = createNumber("notificationId", Long.class);

    public final EnumPath<NotificationType> notificationType = createEnum("notificationType", NotificationType.class);

    public final StringPath opponentMemberHandle = createString("opponentMemberHandle");

    public final StringPath opponentMemberProfile = createString("opponentMemberProfile");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QNotification(String variable) {
        super(Notification.class, forVariable(variable));
    }

    public QNotification(Path<? extends Notification> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotification(PathMetadata metadata) {
        super(Notification.class, metadata);
    }

}


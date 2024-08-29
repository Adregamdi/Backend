package com.adregamdi.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -1591343991L;

    public static final QMember member = new QMember("member1");

    public final com.adregamdi.core.entity.QBaseTime _super = new com.adregamdi.core.entity.QBaseTime(this);

    public final StringPath age = createString("age");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final StringPath gender = createString("gender");

    public final StringPath handle = createString("handle");

    public final ComparablePath<java.util.UUID> memberId = createComparable("memberId", java.util.UUID.class);

    public final BooleanPath memberStatus = createBoolean("memberStatus");

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final StringPath profile = createString("profile");

    public final StringPath refreshToken = createString("refreshToken");

    public final BooleanPath refreshTokenStatus = createBoolean("refreshTokenStatus");

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final StringPath socialAccessToken = createString("socialAccessToken");

    public final StringPath socialId = createString("socialId");

    public final EnumPath<SocialType> socialType = createEnum("socialType", SocialType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}


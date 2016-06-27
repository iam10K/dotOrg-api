package com.dotorg.api.objects;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

/**
 * dotOrg-api
 * Date Created: 5/21/2016
 * |
 * Original Package: com.dotorg.api.objects
 * |
 * COPYRIGHT 2016
 */
@Entity
public class Member {

    @Id
    private Long memberId;

    @Parent
    private Key<Group> groupKey;

    private String userId;

    private Integer memberRole;
    private String nickname;

    private boolean silentMode;

    private boolean kicked;
    private boolean leftGroup;

    private Member() {
    }

    public Member(Key<Group> groupKey, String userId, Integer memberRole, String nickname) {
        this.groupKey = groupKey;
        this.userId = userId;
        this.memberRole = memberRole;
        this.nickname = nickname;
        this.kicked = false;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Member> getKey() {
        return Key.create(Member.class, memberId);
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Long getMemberId() {
        return memberId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Group> getGroupKey() {
        return groupKey;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setGroupKey(Key<Group> groupKey) {
        this.groupKey = groupKey;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public String getUserId() {
        return userId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setUserId(String userId) {
        this.userId = userId;
    }


    public Integer getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(Integer memberRole) {
        this.memberRole = memberRole;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public boolean isSilentMode() {
        return silentMode;
    }

    public void setSilentMode(boolean silentMode) {
        this.silentMode = silentMode;
    }

    public boolean isKicked() {
        return kicked;
    }

    public void setKicked(boolean kicked) {
        this.kicked = kicked;
    }


    public boolean isLeftGroup() {
        return leftGroup;
    }

    public void setLeftGroup(boolean leftGroup) {
        this.leftGroup = leftGroup;
    }
}

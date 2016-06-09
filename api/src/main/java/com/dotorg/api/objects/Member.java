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

    private Key<Group> groupId;

    @Parent
    private Key<User> profileId;

    private String memberRole;
    private String nickname;
    private boolean kicked;

    private Member() {}

    public Long getMemberId() {
        return memberId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Key<Group> getGroupId() {
        return groupId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setGroupId(Key<Group> groupId) {
        this.groupId = groupId;
    }

    public Key<User> getProfileId() {
        return profileId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setProfileId(Key<User> profileId) {
        this.profileId = profileId;
    }

    public String getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isKicked() {
        return kicked;
    }

    public void setKicked(boolean kicked) {
        this.kicked = kicked;
    }
}

package com.dotorg.api.objects;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

/**
 * dotOrg-api
 * Date Created: 6/19/2016
 * |
 * Original Package: com.dotorg.api.objects
 * |
 * COPYRIGHT 2016
 */
@Entity
public class Membership {

    @Id
    private Long membershipId;

    private Long memberId;

    @Index
    private Long groupId;

    @Parent
    private Key<User> userKey;

    private boolean previous;

    public Membership() {
    }

    public Membership(Long memberId, Long groupId, Key<User> userKey) {
        this.memberId = memberId;
        this.groupId = groupId;
        this.userKey = userKey;
        this.previous = false;
    }

    public Key<Membership> getKey() {
        return Key.create(Membership.class, membershipId);
    }

    public Long getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(Long membershipId) {
        this.membershipId = membershipId;
    }


    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Key<Member> getMemberKey() {
        return Key.create(Member.class, memberId);
    }


    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Key<Group> getGroupKey() {
        return Key.create(Group.class, groupId);
    }


    public Key<User> getUserKey() {
        return userKey;
    }

    public void setUserKey(Key<User> userKey) {
        this.userKey = userKey;
    }


    public boolean isPrevious() {
        return previous;
    }

    public void setPrevious(boolean previous) {
        this.previous = previous;
    }
}

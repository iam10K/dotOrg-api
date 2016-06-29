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
public class Speaker {
    @Id
    private Long speakerId;

    private Long memberId;

    private String userId;

    private boolean isMuted;

    private boolean ignore;

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    @Parent
    private Key<Chat> chatKey;

    private Speaker() {
    }

    public Speaker(Long memberId, Key<Chat> chatKey) {
        this.memberId = memberId;
        this.chatKey = chatKey;
        this.isMuted = false;
        this.ignore = false;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Speaker> getKey() {
        return Key.create(Speaker.class, speakerId);
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Long getSpeakerId() {
        return speakerId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setSpeakerId(Long speakerId) {
        this.speakerId = speakerId;
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
    public Key<Member> getMemberKey() {
        return Key.create(Member.class, memberId);
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public String getUserId() {
        return userId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<User> getUserKey() {
        return Key.create(User.class, userId);
    }


    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }


    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Chat> getChatKey() {
        return chatKey;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setChatKey(Key<Chat> chatKey) {
        this.chatKey = chatKey;
    }
}

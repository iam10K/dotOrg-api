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
public class Chatter {
    @Id
    private Long chatterId;

    private Key<Member> memberId;

    private boolean isMuted;

    private boolean ignore;

    @Parent
    private Key<Chat> chatId;

    private Chatter() {}

    public Long getChatterId() {
        return chatterId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setChatterId(Long chatterId) {
        this.chatterId = chatterId;
    }

    public Key<Member> getMemberId() {
        return memberId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setMemberId(Key<Member> memberId) {
        this.memberId = memberId;
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

    public Key<Chat> getChatId() {
        return chatId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setChatId(Key<Chat> chatId) {
        this.chatId = chatId;
    }
}

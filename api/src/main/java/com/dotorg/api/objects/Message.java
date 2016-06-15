package com.dotorg.api.objects;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

import java.util.Date;
import java.util.List;

/**
 * dotOrg-api
 * Date Created: 5/21/2016
 * |
 * Original Package: com.dotorg.api.objects
 * |
 * COPYRIGHT 2016
 */
@Entity
public class Message {

    @Id
    private Long messageId;

    private Date createdAt;

    @Parent
    private Key<Chat> chatId;

    private Long memberId;

    private String text;

    private List<Object> attachments;

    private Message() {}

    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Long getMessageId() {
        return messageId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Chat> getChatId() {
        return chatId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setChatId(Key<Chat> chatId) {
        this.chatId = chatId;
    }


    public Long getMemberId() {
        return memberId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public List<Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Object> attachments) {
        this.attachments = attachments;
    }
}

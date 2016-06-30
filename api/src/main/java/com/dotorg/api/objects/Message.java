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

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    @Parent
    private Key<Chat> chatKey;

    private Long memberId;
    private String userId;

    private String name;
    private String imageUrl;

    private boolean system;

    private String text;

    private List<Object> attachments;

    private Message() {
    }

    public Message(Chat chat, User user, Member member, String text, List<Object> attachments) {
        this.createdAt = new Date();
        this.chatKey = chat.getKey();
        this.memberId = member.getMemberId();
        this.userId = user.getUserId();
        this.name = member.getNickname();
        this.imageUrl = user.getImageUrl();
        this.text = text;
        this.attachments = attachments;
        this.system = false;
    }

    public Message(Chat chat, String text, List<Object> attachments) {
        this.createdAt = new Date();
        this.chatKey = chat.getKey();
        this.memberId = -1L;
        this.userId = "";
        this.name = "";
        this.imageUrl = ""; // TODO: Default image url
        this.text = text;
        this.attachments = attachments;
        this.system = true;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Message> getKey() {
        return Key.create(Message.class, messageId);
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Long getMessageId() {
        return messageId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Date getCreatedAt() {
        return createdAt;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Chat> getChatKey() {
        return chatKey;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setChatKey(Key<Chat> chatKey) {
        this.chatKey = chatKey;
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


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public String getName() {
        return name;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setName(String name) {
        this.name = name;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public String getImageUrl() {
        return imageUrl;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public boolean isSystem() {
        return system;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setSystem(boolean system) {
        this.system = system;
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

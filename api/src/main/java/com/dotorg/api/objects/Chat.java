package com.dotorg.api.objects;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Parent;

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
public class Chat {

    @Id
    private Long chatId;

    private String name;

    private String description;

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    @Parent
    private Key<Group> groupKey;

    @Ignore
    private List<Speaker> speakers;

    private Chat() {
    }

    public Chat(String name, Key<Group> groupKey) {
        this.name = name;
        this.groupKey = groupKey;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Chat> getKey() {
        return Key.create(Chat.class, chatId);
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Long getChatId() {
        return chatId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    public List<Speaker> getSpeakers() {
        return speakers;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }
}

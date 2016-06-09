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

    @Parent
    private Key<Group> groupId;

    @Ignore
    private List<Chatter> chatters;

    private Chat() {}

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

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Group> getGroupId() {
        return groupId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setGroupId(Key<Group> groupId) {
        this.groupId = groupId;
    }

    public List<Chatter> getChatters() {
        return chatters;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setChatters(List<Chatter> chatters) {
        this.chatters = chatters;
    }
}

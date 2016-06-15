package com.dotorg.api.objects;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;

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
public class Group {

    @Id
    private Long groupId;
    private Long creator;

    private Date createdAt;
    private Date modifiedAt;

    private String name;
    private String description;
    private String imageUrl;
    private boolean publicGroup;

    private String joinUrl;

    @Ignore
    private List<Member> members;
    @Ignore
    private List<Chat> chats;
    @Ignore
    private List<News> news;
    @Ignore
    private List<Poll> polls;
    @Ignore
    private List<Event> events;

    private Group() {}

    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Long getGroupId() {
        return groupId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Long getCreator() {
        return creator;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setCreator(Long creator) {
        this.creator = creator;
    }


    public boolean isPublicGroup() {
        return publicGroup;
    }

    public void setPublicGroup(boolean publicGroup) {
        this.publicGroup = publicGroup;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public String getJoinUrl() {
        return joinUrl;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setJoinUrl(String joinUrl) {
        this.joinUrl = joinUrl;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Date getCreatedAt() {
        return createdAt;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Date getModifiedAt() {
        return modifiedAt;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public List<Member> getMembers() {
        return members;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setMembers(List<Member> members) {
        this.members = members;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public List<Chat> getChats() {
        return chats;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public List<News> getNews() {
        return news;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setNews(List<News> news) {
        this.news = news;
    }


    public List<Poll> getPolls() {
        return polls;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setPolls(List<Poll> polls) {
        this.polls = polls;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public List<Event> getEvents() {
        return events;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setEvents(List<Event> events) {
        this.events = events;
    }
}

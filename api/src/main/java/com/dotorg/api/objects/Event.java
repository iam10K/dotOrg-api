package com.dotorg.api.objects;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

import java.util.Date;

/**
 * dotOrg-api
 * Date Created: 5/21/2016
 * |
 * Original Package: com.dotorg.api.objects
 * |
 * COPYRIGHT 2016
 */
@Entity
public class Event {

    @Id
    private Long eventId;

    private Date createdAt;

    private Date startDate;
    private Date endDate;

    private String title;
    private String description;
    private GeoPt location;

    private Long memberId;
    private String name;
    private String imageUrl;

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    @Parent
    private Key<Group> groupKey;

    private Event() {}

    public void createNewEvent(Member member, User user, Key<Group> groupKey) {
        this.createdAt = new Date();
        this.memberId = member.getMemberId();
        this.name = member.getNickname();
        this.imageUrl = user.getImageUrl();
        this.groupKey = groupKey;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Event> getKey() {
        return Key.create(Event.class, eventId);
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Long getEventId() {
        return eventId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Date getCreatedAt() {
        return createdAt;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }


    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public GeoPt getLocation() {
        return location;
    }

    public void setLocation(GeoPt location) {
        this.location = location;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Long getMemberId() {
        return memberId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Group> getGroupKey() {
        return groupKey;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setGroupKey(Key<Group> groupKey) {
        this.groupKey = groupKey;
    }
}

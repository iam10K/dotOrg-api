package com.dotorg.api.objects;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
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
public class News {

    @Id
    private Long newsId;

    private String title;
    private String description;

    private Key<Member> memberId;

    private List<Object> attachments;

    @Parent
    private Key<Group> groupId;

    private News() {}

    public Long getNewsId() {
        return newsId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setNewsId(Long newsId) {
        this.newsId = newsId;
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

    public Key<Member> getMemberId() {
        return memberId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setMemberId(Key<Member> memberId) {
        this.memberId = memberId;
    }

    public List<Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Object> attachments) {
        this.attachments = attachments;
    }

    public Key<Group> getGroupId() {
        return groupId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setGroupId(Key<Group> groupId) {
        this.groupId = groupId;
    }
}

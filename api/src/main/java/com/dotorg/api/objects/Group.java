package com.dotorg.api.objects;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;

import java.util.ArrayList;
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
    private String creator;

    private Date createdAt;
    private Date modifiedAt;

    private String modifiedBy;

    private String name;
    private String description;
    private String imageUrl;

    private boolean inviteOnly;

    @Ignore
    private boolean makeOpen;

    private String shareToken;

    @Ignore
    private String joinUrl;


    @Ignore
    private List<Member> members = new ArrayList<>();
    @Ignore
    private Integer membersCount;
    @Ignore
    private List<Chat> chats = new ArrayList<>();
    @Ignore
    private List<News> news = new ArrayList<>();
    @Ignore
    private List<Poll> polls = new ArrayList<>();
    @Ignore
    private List<Event> events = new ArrayList<>();

    private Group() {
    }

    public void createNewGroup(String creator, String shareToken) {
        this.creator = creator;
        this.createdAt = new Date();
        this.modifiedAt = new Date();

        if (description == null)
            description = "";

        this.shareToken = shareToken;
    }

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


    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public String getCreator() {
        return creator;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setCreator(String creator) {
        this.creator = creator;
    }


    public boolean isInviteOnly() {
        return inviteOnly;
    }

    public void setInviteOnly(boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public boolean isMakeOpen() {
        return makeOpen;
    }

    public void setMakeOpen(boolean makeOpen) {
        this.makeOpen = makeOpen;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public String getShareToken() {
        return shareToken;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setShareToken(String shareToken) {
        this.shareToken = shareToken;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public String getJoinUrl() {
        if (inviteOnly) {
            return "";
        } else {
            return "https://dotorg.com/join_group/" + groupId + "/" + shareToken;
        }
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


    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public String getModifiedBy() {
        return modifiedBy;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public List<Member> getMembers() {
        return members;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setMembers(List<Member> members) {
        this.members = members;
    }

/*    @ApiResourceProperty(ignored =  AnnotationBoolean.TRUE)
    public Member addMember(User user) {
        Member member = new Member(newGroup.getGroupId(), user.getUserId(), 0, user.getName());
        ofy().save().entity(member).now();

    }*/


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

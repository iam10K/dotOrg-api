package com.dotorg.api.objects;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
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
public class Poll {

    @Id
    private Long pollId;

    @Parent
    private Key<Group> groupKey;

    private String title;
    private String description;

    private Date createdAt;
    private Date endDate;

    private boolean anonymous;

    private Long memberId;
    private String name;
    private String imageUrl;

    @Ignore
    private List<Choice> choices;

    @Ignore
    private List<Vote> voteList;


    private Poll() {
    }

    public void createNewPoll(Member member, User user, Key<Group> groupKey) {
        this.createdAt = new Date();
        this.memberId = member.getMemberId();
        this.name = member.getNickname();
        this.imageUrl = user.getImageUrl();
        this.groupKey = groupKey;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Poll> getKey() {
        return Key.create(Poll.class, pollId);
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Long getPollId() {
        return pollId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setPollId(Long pollId) {
        this.pollId = pollId;
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


    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Date getCreatedAt() {
        return createdAt;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
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


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public List<Choice> getChoices() {
        return choices;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public void setChoices(List<Choice> choices) {
        this.choices = choices;
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
    public List<Vote> getVoteList() {
        return voteList;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setVoteList(List<Vote> voteList) {
        this.voteList = voteList;
    }
}

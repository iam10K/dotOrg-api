package com.dotorg.api.objects;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

/**
 * dotOrg-api
 * Date Created: 6/28/2016
 * |
 * Original Package: com.dotorg.api.objects
 * |
 * COPYRIGHT 2016
 */
public class Vote {

    @Id
    private Long voteId;

    @Parent
    private Key<Poll> pollKey;

    private Long memberId;
    private String name;
    private String imageUrl;

    private Long choiceId;

    @Index
    private String userId;

    public Vote(Key<Poll> pollKey, Member member, User user, Long choiceId) {
        this.pollKey = pollKey;
        this.memberId = member.getMemberId();
        this.name = member.getNickname();
        this.imageUrl = user.getImageUrl();
        this.choiceId = choiceId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Vote> getKey() {
        return Key.create(Vote.class, voteId);
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Long getVoteId() {
        return voteId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setVoteId(Long voteId) {
        this.voteId = voteId;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Poll> getPollKey() {
        return pollKey;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setPollKey(Key<Poll> pollKey) {
        this.pollKey = pollKey;
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
    public Long getChoiceId() {
        return choiceId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setChoiceId(Long choiceId) {
        this.choiceId = choiceId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Choice> getChoiceKey() {
        return Key.create(Choice.class, choiceId);
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
}

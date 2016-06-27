package com.dotorg.api.objects;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

import java.util.Collection;

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

    private String title;
    private String description;

    private Collection<String> questions;

    private Collection<Integer> votes;

    //private boolean anonymous;

    private Long memberId;

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    @Parent
    private Key<Group> groupKey;

    private Poll() {
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


    public Collection<String> getQuestions() {
        return questions;
    }

    public void setQuestions(Collection<String> questions) {
        this.questions = questions;
    }


    public Collection<Integer> getVotes() {
        return votes;
    }

    public void setVotes(Collection<Integer> votes) {
        this.votes = votes;
    }


    /*public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }*/

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


    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Group> getGroupKey() {
        return groupKey;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setGroupKey(Key<Group> groupKey) {
        this.groupKey = groupKey;
    }
}

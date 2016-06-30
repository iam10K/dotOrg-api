package com.dotorg.api.objects;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Parent;

/**
 * dotOrg-api
 * Date Created: 6/28/2016
 * |
 * Original Package: com.dotorg.api.objects
 * |
 * COPYRIGHT 2016
 */
public class Choice {

    @Id
    private Long choiceId;

    @Parent
    private Key<Poll> pollKey;

    private String text;

    @Ignore
    private Long votes;

    public Choice() {

    }

    public void createNewChoice(Key<Poll> pollKey) {
        this.choiceId = null;
        this.pollKey = pollKey;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Choice> getKey() {
        return Key.create(Choice.class, choiceId);
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Long getChoiceId() {
        return choiceId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public void setChoiceId(Long choiceId) {
        this.choiceId = choiceId;
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
    public String getText() {
        return text;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setText(String text) {
        this.text = text;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public Long getVotes() {
        return votes;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setVotes(Long votes) {
        this.votes = votes;
    }
}

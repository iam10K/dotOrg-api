package com.dotorg.api.objects;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

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
public class User {

    @Id
    private String userId;

    private String email;

    private String name;

    private String imageUrl;

    private List<Key<Group>> groups;

    public User() {}

    public User(String userId) {
        this.userId = userId;
    }

    public User(String userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public String getUserId() {
        return userId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setUserId(String userId) {
        this.userId = userId;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public String getEmail() {
        return email;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setEmail(String email) {
        this.email = email;
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
    public List<Key<Group>> getGroups() {
        return groups;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setGroups(List<Key<Group>> groups) {
        this.groups = groups;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void addGroup(Key<Group> group) {
        this.groups.add(group);
    }


}

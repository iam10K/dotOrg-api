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
    private String profileId;

    private String email;

    private String name;

    private String imageUrl;

    private List<Key<Group>> groups;

    private User() {}

    public User(String profileId) {
        this.profileId = profileId;
    }

    public User(String profileId, String email, String name) {
        this.profileId = profileId;
        this.email = email;
        this.name = name;
    }

    public String getProfileId() {
        return profileId;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getEmail() {
        return email;
    }

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

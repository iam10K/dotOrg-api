package com.dotorg.api.objects;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.datastore.PhoneNumber;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

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

    @Index
    private String email;

    @Index
    private PhoneNumber phoneNumber;

    private String name;

    private String imageUrl;

    @Ignore
    private List<Long> groups;
    @Ignore
    private List<Long> previousGroups;

    public User() {
    }

    public User(String userId) {
        this.userId = userId;
    }

    public User(String userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.groups = new ArrayList<>();
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<User> getKey() {
        return Key.create(User.class, userId);
    }


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


    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
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
    public List<Long> getGroups() {
        if (groups == null) {
            loadGroups();
        }
        return groups;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setGroups(List<Long> groups) {
        this.groups = groups;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.FALSE)
    public List<Long> getPreviousGroups() {
        if (previousGroups == null) {
            loadGroups();
        }
        return previousGroups;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setPreviousGroups(List<Long> previousGroups) {
        this.previousGroups = previousGroups;
    }


    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void loadGroups() {
        groups = new ArrayList<>();
        previousGroups = new ArrayList<>();
        List<Membership> memberships = getMemberships();
        groups.clear();
        previousGroups.clear();
        for (Membership membership : memberships) {
            if (membership.isPrevious()) {
                previousGroups.add(membership.getGroupId());
            } else {
                groups.add(membership.getGroupId());
            }
        }
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Member addGroup(Long groupId) {
        // Add member to group
        Member member = new Member(Key.create(Group.class, groupId), getUserId(), 2, getName());
        // Use objectify to create entity(Member)
        ofy().save().entity(member).now();

        member = ofy().load().entity(member).now();

        Membership membership = new Membership(member.getMemberId(), groupId, getKey());
        ofy().save().entity(membership).now();
        return member;
    }
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public List<Membership> getMemberships() {
        return ofy().load().type(Membership.class).ancestor(this).list();
    }
}

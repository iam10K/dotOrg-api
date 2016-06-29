package com.dotorg.api.endpoints.v1;

import com.dotorg.api.exceptions.InvalidParameterException;
import com.dotorg.api.objects.Chat;
import com.dotorg.api.objects.ChatMembership;
import com.dotorg.api.objects.Speaker;
import com.dotorg.api.objects.Event;
import com.dotorg.api.objects.Group;
import com.dotorg.api.objects.Member;
import com.dotorg.api.objects.Membership;
import com.dotorg.api.objects.Message;
import com.dotorg.api.objects.News;
import com.dotorg.api.objects.Poll;
import com.dotorg.api.objects.User;
import com.dotorg.api.utils.ValidationHelper;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */

@ApiReference(BaseEndpointV1.class)
public class GroupEndpointV1 {

    private static final Logger logger = Logger.getLogger(GroupEndpointV1.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 10;
    private static final int DEFAULT_LONG_LIST_LIMIT = 200;

    /**
     * Returns the {@link Group} with the corresponding ID.
     *
     * @param groupId the ID of the entity to be retrieved
     * @param token   token for current session
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Group} with the provided ID.
     */
    @ApiMethod(
            name = "groups.get",
            path = "groups/{groupId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Group get(@Named("groupId") Long groupId, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        // Load group by id
        logger.info("Getting Group with ID: " + groupId);
        Group group = ofy().load().type(Group.class).id(groupId).now();
        if (group == null) {
            throw new NotFoundException("Could not find Group with ID: " + groupId);
        }

        // Load group data
        loadGroupData(group);

        return group;
    }

    /**
     * Inserts a new {@code Group}.
     *
     * @param token token for current session
     */
    @ApiMethod(
            name = "groups.create",
            path = "groups",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Group create(Group group, @Named("token") String token) throws UnauthorizedException, InvalidParameterException, NotFoundException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateNewGroup(group);

        return createGroup(group, user);
    }

    /**
     * Updates an existing {@code Group}.
     *
     * @param newGroup the desired state of the entity
     * @param groupId  the ID of the entity to be updated
     * @param token    token for current session
     * @return the updated version of the entity
     * @throws NotFoundException     if the {@code groupId} does not correspond to an existing
     *                               {@code Group}
     * @throws UnauthorizedException
     */
    @ApiMethod(
            name = "groups.update",
            path = "groups/{groupId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Group update(Group newGroup, @Named("groupId") Long groupId, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        // FUTURE: Permissions to change group
        ValidationHelper.validateUserInGroup(user, groupId);

        Group group = ofy().load().type(Group.class).id(groupId).now();
        if (group == null) {
            throw new NotFoundException("Could not find Group with ID: " + groupId);
        }

        return updateGroup(group, newGroup, user);
    }

    /**
     * Deletes the specified {@code Group}.
     *
     * @param groupId the ID of the entity to delete
     * @param token   token for current session
     * @throws NotFoundException if the {@code groupId} does not correspond to an existing
     *                           {@code Group}
     */
    @ApiMethod(
            name = "groups.delete",
            path = "groups/{groupId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void delete(@Named("groupId") Long groupId, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        Group group = ofy().load().type(Group.class).id(groupId).now();
        if (group == null) {
            throw new NotFoundException("Could not find Group with ID: " + groupId);
        }

        // FUTURE: Admin permission to delete group
        ValidationHelper.validateUserIsGroupOwner(user, group);

        removeGroup(group, user);
        logger.info(user.getUserId() + ", Deleted group with ID: " + groupId);
    }

    /**
     * List all entities.
     *
     * @param limit the maximum number of entries to return
     * @param token token for current session
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "groups.list",
            path = "groups",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Group> list(@Nullable @Named("cursor") Integer cursor, @Nullable @Named("limit") Integer limit, @Named("token") String token) throws UnauthorizedException, NotFoundException {
        User user = ValidationHelper.validateToken(token);

        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        cursor = cursor == null ? 0 : cursor;

        // Get list of Keys based on limit and cursor
        List<Long> idList = new ArrayList<>(limit);
        for (int i = cursor; i < limit && i < user.getGroups().size(); i++) {
            idList.add(user.getGroups().get(i));
        }

        // Load groups by id
        Collection<Group> groupList = ofy().load().type(Group.class).ids(idList).values();
        // Load group data
        for (Group group : groupList) {
            loadGroupData(group);
        }

        return CollectionResponse.<Group>builder().setItems(groupList).build();
    }

    @ApiMethod(
            name = "groups.join",
            path = "groups/{groupId}/join/{shareToken}",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Group join(@Named("groupId") Long groupId, @Named("shareToken") String shareToken, @Named("token") String token) throws NotFoundException, UnauthorizedException, BadRequestException {
        User user = ValidationHelper.validateToken(token);

        Group group = ofy().load().type(Group.class).id(groupId).now();
        if (group == null) {
            throw new NotFoundException("Could not find Group with ID: " + groupId);
        }

        ValidationHelper.validateUserNotInGroup(user, groupId);

        return joinGroup(group, user, shareToken);
    }

    @ApiMethod(
            name = "groups.rejoin",
            path = "groups/{groupId}/join",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Group rejoin(@Named("groupId") Long groupId, @Named("token") String token) throws NotFoundException, UnauthorizedException, BadRequestException {
        User user = ValidationHelper.validateToken(token);

        Group group = ofy().load().type(Group.class).id(groupId).now();
        if (group == null) {
            throw new NotFoundException("Could not find Group with ID: " + groupId);
        }

        ValidationHelper.validateUserWasInGroup(user, groupId);

        return rejoinGroup(group, user);
    }

    private void loadGroupData(Group group) {
        // Load data for each of the following arrays, Limit to not send too large of a response
        // TODO: Change this to be more in line with gme
        group.setMembers(ofy().load().type(Member.class).ancestor(group).limit(DEFAULT_LONG_LIST_LIMIT).list());
        group.setChats(ofy().load().type(Chat.class).ancestor(group).limit(DEFAULT_LONG_LIST_LIMIT).list());
        group.setPolls(ofy().load().type(Poll.class).ancestor(group).limit(DEFAULT_LONG_LIST_LIMIT).list());
        group.setEvents(ofy().load().type(Event.class).ancestor(group).limit(DEFAULT_LONG_LIST_LIMIT).list());
        group.setNews(ofy().load().type(News.class).ancestor(group).limit(DEFAULT_LONG_LIST_LIMIT).list());
    }

    private Group createGroup(Group group, User user) {
        // Set group created at
        group.createNewGroup(user.getUserId(), group.isMakeOpen() ? "" : generateJoinToken());

        // Use objectify to create entity(Group)
        Key<Group> groupKey = ofy().save().entity(group).now();

        // Load the new group based on Key
        Group newGroup = ofy().load().key(groupKey).now();
        logger.info("Created Group: " + newGroup.getGroupId());

        // Add user to group
        Member member = user.addGroup(group.getGroupId());

        // Add member to group list
        // TODO: This will change with above todo also
        List<Member> members = new ArrayList<>();
        members.add(member);
        newGroup.setMembers(members);

        return newGroup;
    }

    private Group updateGroup(Group group, Group newGroup, User user) {
        boolean changes = false;

        // If group name is being changed
        if (newGroup.getName() != null) {
            changes = true;
            group.setName(newGroup.getName());
            // TODO: Push notification, maybe add message in news?
        }

        // If description is being changed
        if (newGroup.getDescription() != null) {
            changes = true;
            group.setDescription(newGroup.getDescription());
            // TODO: Push notification, maybe add message in news?
        }

        // If image url is being set
        if (newGroup.getImageUrl() != null) {
            changes = true;
            group.setImageUrl(newGroup.getImageUrl());
            // TODO: Push notification, updated group image
        }

        // If the group will be make private
        if (newGroup.isInviteOnly() && !group.isInviteOnly()) {
            changes = true;
            group.setInviteOnly(true);
            group.setShareToken("");
            // TODO: Push notification group is now private
        }

        // If the group will be made public
        if (newGroup.isMakeOpen()) {
            changes = true;
            group.setInviteOnly(false);
            group.setShareToken(generateJoinToken());
            // TODO: Push notification group is now public
        }

        if (changes) {
            group.setModifiedAt(new Date());
            group.setModifiedBy(user.getUserId());
            ofy().save().entity(group).now();
            logger.info("Updated Group: " + group.getGroupId());
        }
        return group;
    }

    private void removeGroup(Group group, User user) {
        // Delete all data belonging to the group
        ofy().delete().type(Member.class).parent(group);
        for (Chat chat : ofy().load().type(Chat.class).ancestor(group)) {
            List<ChatMembership> chatMembershipList = ofy().load().type(ChatMembership.class).filter("chatId", chat.getChatId()).list();
            ofy().delete().entities(chatMembershipList).now();
            ofy().delete().type(Speaker.class).parent(chat);
            ofy().delete().type(Message.class).parent(chat);
            ofy().delete().entity(chat);
        }
        ofy().delete().type(Poll.class).parent(group);
        ofy().delete().type(Event.class).parent(group);
        ofy().delete().type(News.class).parent(group);
        for (Membership membership : ofy().load().type(Membership.class).filter("groupId", group.getGroupId()).list()) {
            ofy().delete().entity(membership);
        }
        ofy().delete().entity(group);
    }

    private Group joinGroup(Group group, User user, String shareToken) throws BadRequestException {
        if (!group.getShareToken().equals(shareToken)) {
            throw new BadRequestException("Share token for this group is invalid.");
        }

        // Add member to group
        Member member = new Member(Key.create(Group.class, group.getGroupId()), user.getUserId(), 2, user.getName());
        // Use objectify to create entity(Member)
        ofy().save().entity(member).now();

        // TODO: Push notification user has joined group?

        loadGroupData(group);

        return group;
    }

    private Group rejoinGroup(Group group, User user) throws UnauthorizedException {
        // Add group to user
        Membership membership = ofy().load().type(Membership.class).ancestor(user).filter("groupId", group.getGroupId()).first().now();

        membership.setPrevious(false);

        ofy().save().entity(membership);

        // TODO: Push notification user has rejoined group?

        loadGroupData(group);

        return group;
    }

    private String generateJoinToken() {
        char[] chars = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ234567890".toCharArray();
        SecureRandom random2 = new SecureRandom();
        String s = "";
        for (int i = 0; i < 6; i++) {
            s += chars[random2.nextInt(chars.length)];
        }
        return s;
    }

}
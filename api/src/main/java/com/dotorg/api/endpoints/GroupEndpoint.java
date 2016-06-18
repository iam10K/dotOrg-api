package com.dotorg.api.endpoints;

import com.dotorg.api.exceptions.InvalidParameterException;
import com.dotorg.api.objects.Group;
import com.dotorg.api.objects.Member;
import com.dotorg.api.objects.User;
import com.dotorg.api.utils.ValidationHelper;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.googlecode.objectify.Key;

import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
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
@Api(
        name = "groups",
        version = "v1",
        resource = "group",
        namespace = @ApiNamespace(
                ownerDomain = "objects.api.dotorg.com",
                ownerName = "objects.api.dotorg.com",
                packagePath = ""
        )
)
public class GroupEndpoint {

    private static final Logger logger = Logger.getLogger(GroupEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 10;

    private FirebaseOptions options;

    public GroupEndpoint() {
        try {
            options = new FirebaseOptions.Builder()
                    .setServiceAccount(new FileInputStream("WEB-INF/firebase/dotOrg-API.json"))
                    .setDatabaseUrl("https://dotorg-api.firebaseio.com/")
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the {@link Group} with the corresponding ID.
     *
     * @param groupId the ID of the entity to be retrieved
     * @param token  token for current session
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Group} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "groups/{groupId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Group get(@Named("groupId") Long groupId, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        // Load group by id
        logger.info("Getting Group with ID: " + groupId);
        Group group = ofy().load().type(Group.class).id(groupId).now();
        if (group == null) {
            throw new NotFoundException("Could not find Group with ID: " + groupId);
        }

        // Validate user is in group, if not throw unauthorized
        if (!user.getGroups().contains(groupId)) {
            throw new UnauthorizedException("Authorized user does not have access to this group.");
        }

        // Load group data
        loadGroup(group);

        return group;
    }

    /**
     * Inserts a new {@code Group}.
     *
     * @param token  token for current session
     */
    @ApiMethod(
            name = "create",
            path = "groups",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Group create(Group group,
                        @Named("token") String token) throws UnauthorizedException, InvalidParameterException, NotFoundException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateNewGroup(group);

        return createGroup(group, user);
    }

    /**
     * Updates an existing {@code Group}.
     *
     * @param group   the desired state of the entity
     * @param groupId the ID of the entity to be updated
     * @param token  token for current session
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code groupId} does not correspond to an existing
     *                           {@code Group}
     * @throws UnauthorizedException
     */
    @ApiMethod(
            name = "update",
            path = "groups/{groupId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Group update(Group group,
                        @Named("groupId") Long groupId,
                        @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(groupId);
        ofy().save().entity(group).now();
        logger.info("Updated Group: " + group);
        return ofy().load().entity(group).now();
    }

    /**
     * Deletes the specified {@code Group}.
     *
     * @param groupId the ID of the entity to delete
     * @param token  token for current session
     * @throws NotFoundException if the {@code groupId} does not correspond to an existing
     *                           {@code Group}
     */
    @ApiMethod(
            name = "remove",
            path = "groups/{groupId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("groupId") Long groupId,
                       @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        checkExists(groupId);
        ofy().delete().type(Group.class).id(groupId).now();
        logger.info("Deleted Group with ID: " + groupId);
    }

    /**
     * List all entities.
     *
     * @param limit  the maximum number of entries to return
     * @param token  token for current session
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "groups",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Group> list(@Nullable @Named("cursor") Integer cursor,
                                          @Nullable @Named("limit") Integer limit,
                                          @Named("token") String token) throws UnauthorizedException, NotFoundException {
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
            loadGroup(group);
        }

        return CollectionResponse.<Group>builder().setItems(groupList).build();
    }

    private void checkExists(Long groupId) throws NotFoundException {
        try {
            ofy().load().type(Group.class).id(groupId).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Group with ID: " + groupId);
        }
    }



    private void loadGroup(Group group) {
        // TODO: Load group members, chats, polls, events, news
    }

    private Group createGroup(Group group, User user) {
        // Set group created at
        group.createNewGroup(user.getUserId(), group.isInviteOnly() ? "" : generateJoinToken());

        // Use objectify to create entity(Group)
        Key<Group> groupKey = ofy().save().entity(group).now();

        // Load the new group based on Key
        Group newGroup = ofy().load().key(groupKey).now();
        logger.info("Created Group: " + newGroup.getGroupId());

        // Add member to group
        Member member = new Member(groupKey, user.getUserId(), 0, user.getName());
        // Use objectify to create entity(Member)
        ofy().save().entity(member).now();

        // Add member to group list
        List<Member> members = new ArrayList<>();
        members.add(member);
        newGroup.setMembers(members);

        // Add group to user
        user.addGroup(group.getGroupId());

        return newGroup;
    }

    private String generateJoinToken () {
        char[] chars = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ234567890".toCharArray();
        SecureRandom random2 = new SecureRandom();
        String s = "";
        for (int i = 0; i < 6; i++) {
            s += chars[random2.nextInt(chars.length)];
        }
        return s;
    }

}
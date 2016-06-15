package com.dotorg.api.endpoints;

import com.dotorg.api.objects.Group;
import com.dotorg.api.objects.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.tasks.Task;
import com.googlecode.objectify.cmd.Query;

import java.io.FileInputStream;
import java.util.ArrayList;
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
        resource = "groups",
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
        User user;
        try {
            user = validateToken(token);
        } catch (Exception ex) {
            throw new UnauthorizedException("Could not validate token.");
        }

        logger.info("Getting Group with ID: " + groupId);
        Group group = ofy().load().type(Group.class).id(groupId).now();
        if (group == null) {
            throw new NotFoundException("Could not find Group with ID: " + groupId);
        }
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
    public Group create(Group group, @Named("token") String token) throws UnauthorizedException {
        User user;
        try {
            user = validateToken(token);
        } catch (Exception ex) {
            throw new UnauthorizedException("Could not validate token.");
        }
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that group.groupId has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(group).now();
        logger.info("Created Group.");

        return ofy().load().entity(group).now();
    }

    /**
     * Updates an existing {@code Group}.
     *
     * @param groupId the ID of the entity to be updated
     * @param group   the desired state of the entity
     * @param token  token for current session
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code groupId} does not correspond to an existing
     *                           {@code Group}
     */
    @ApiMethod(
            name = "update",
            path = "groups/{groupId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Group update(@Named("groupId") Long groupId, Group group, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user;
        try {
            user = validateToken(token);
        } catch (Exception ex) {
            throw ex;
        }
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
    public void remove(@Named("groupId") Long groupId, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user;
        try {
            user = validateToken(token);
        } catch (Exception ex) {
            throw new UnauthorizedException("Could not validate token.");
        }

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
    public CollectionResponse<Group> list(@Nullable @Named("limit") Integer limit, @Named("token") String token) throws UnauthorizedException {
        User user;
        try {
            user = validateToken(token);
        } catch (Exception ex) {
            throw new UnauthorizedException("Could not validate token.");
        }

        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Group> query = ofy().load().type(Group.class).limit(limit);
        QueryResultIterator<Group> queryIterator = query.iterator();
        List<Group> groupList = new ArrayList<Group>(limit);
        while (queryIterator.hasNext()) {
            groupList.add(queryIterator.next());
        }
        return CollectionResponse.<Group>builder().setItems(groupList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();

        // TODO: Get list of groups based on authenticated user, then for each group set data like list of members, chats, etc.
        // TODO: Limit to 5 groups per page
    }

    private void checkExists(Long groupId) throws NotFoundException {
        try {
            ofy().load().type(Group.class).id(groupId).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Group with ID: " + groupId);
        }
    }

    private User validateToken (String token) throws NotFoundException, UnauthorizedException {
        Task<FirebaseToken> tokenTask = FirebaseAuth.getInstance().verifyIdToken(token);

        while (!tokenTask.isComplete()) {
        }

        FirebaseToken firebaseToken = tokenTask.getResult();

        User user;

        if (firebaseToken == null) {
            throw new UnauthorizedException("Invalid token. Access Denied.");
        }

        user = ofy().load().type(User.class).id(firebaseToken.getUid()).now();

        if (user == null) {
            throw new NotFoundException("User not found for Token.");
        }
        return user;
    }
}
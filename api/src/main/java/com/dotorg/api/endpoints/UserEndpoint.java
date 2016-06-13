package com.dotorg.api.endpoints;

import com.dotorg.api.objects.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.tasks.Task;
import com.googlecode.objectify.ObjectifyService;

import java.util.logging.Logger;

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
        name = "users",
        version = "v1",
        resource = "user",
        namespace = @ApiNamespace(
                ownerDomain = "objects.api.dotorg.com",
                ownerName = "objects.api.dotorg.com",
                packagePath = ""
        )
)
public class UserEndpoint {

    private static final Logger logger = Logger.getLogger(UserEndpoint.class.getName());
    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        ObjectifyService.register(User.class);
    }

    /**
     * Returns the {@link User} with the corresponding ID.
     *
     * @param token of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code User} with the provided ID.
     */
    @ApiMethod(
            name = "me",
            path = "users/me",
            httpMethod = ApiMethod.HttpMethod.GET)
    public User me(@Named("token") String token) throws NotFoundException, UnauthorizedException {
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
            User newUser = new User(firebaseToken.getUid(), firebaseToken.getEmail(), firebaseToken.getName());
            ofy().save().entity(newUser).now();
            logger.info("Created User: " + newUser.getEmail());
            user = ofy().load().entity(newUser).now();
        }

        if (user == null) {
            throw new NotFoundException("Could not validate token. Access Denied.");
        }
        return user;
    }

    /**
     * Updates users account {@code User}.
     *
     * @param user  the desired state of the entity
     * @param token token for current session
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code profileId} does not correspond to an existing
     *                           {@code User}
     */
    @ApiMethod(
            name = "update",
            path = "users/update",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public User update(User user, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        User validateUser = validateToken(token);
        checkExists(validateUser.getUserId());
        ofy().save().entity(user).now();
        logger.info("Updated User: " + user);
        return ofy().load().entity(user).now();
    }

    private void checkExists(String profileId) throws NotFoundException {
        try {
            ofy().load().type(User.class).id(profileId).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find User with ID: " + profileId);
        }
    }

    private User validateToken(String token) throws NotFoundException, UnauthorizedException {
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
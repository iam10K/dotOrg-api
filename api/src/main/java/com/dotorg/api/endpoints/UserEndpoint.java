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
import javax.servlet.http.HttpServletRequest;

import static com.googlecode.objectify.ObjectifyService.ofy;

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
     * Returns the authenticated {@link User}.
     *
     * @param token token for current session
     * @return the {@code User} that is currently authenticated.
     * @throws NotFoundException if there is no {@code User} for the token.
     * @throws UnauthorizedException if the token is invalid.
     */
    @ApiMethod(
            name = "me",
            path = "users/me",
            httpMethod = ApiMethod.HttpMethod.GET)
    public User me(HttpServletRequest req, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        Task<FirebaseToken> tokenTask = FirebaseAuth.getInstance().verifyIdToken(token);

        while (!tokenTask.isComplete()) {
        }

        FirebaseToken firebaseToken = null;
        try {
            firebaseToken = tokenTask.getResult();
        } catch (IllegalArgumentException ex) {
            throw new UnauthorizedException("Invalid token. Access Denied.");
        }

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
            throw new NotFoundException("Could not find/create user for token. Access Denied.");
        }
        return user;
    }

    /**
     * Updates the authenticated users account {@link User}.
     *
     * @param user  the desired state of the {@code User}
     * @param token token for current session
     * @return the updated version of the {@code User}
     * @throws NotFoundException if there is no {@code User} for the token.
     * @throws UnauthorizedException if the token is invalid.
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
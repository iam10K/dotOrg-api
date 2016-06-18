package com.dotorg.api.endpoints;

import com.dotorg.api.objects.User;
import com.dotorg.api.utils.ValidationHelper;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.tasks.Task;

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

    /**
     * Returns the authenticated {@link User}.
     *
     * @param token token for current session
     * @return the {@code User} that is currently authenticated.
     * @throws NotFoundException     if there is no {@code User} for the token.
     * @throws UnauthorizedException if the token is invalid.
     */
    @ApiMethod(
            name = "me",
            path = "users/me",
            httpMethod = ApiMethod.HttpMethod.GET)
    public User me(HttpServletRequest req, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        Task<FirebaseToken> tokenTask = FirebaseAuth.getInstance().verifyIdToken(token);

        while (true) {
            if (tokenTask.isComplete()) break;
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
     * @param newUser the desired state of the {@code User}
     * @param token   token for current session
     * @return the updated version of the {@code User}
     * @throws NotFoundException     if there is no {@code User} for the token.
     * @throws UnauthorizedException if the token is invalid.
     */
    @ApiMethod(
            name = "update",
            path = "users/update",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public User update(User newUser, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        return updateUser(user, newUser);
    }

    private User updateUser(User user, User newUser) {
        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }

        if (newUser.getImageUrl() != null) {
            user.setImageUrl(newUser.getImageUrl());
        }

        if (newUser.getEmail() != null) {
            // TODO: Requires updating with firebase
        }

        ofy().save().entity(user).now();
        return user;
    }
}
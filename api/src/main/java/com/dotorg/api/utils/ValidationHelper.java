package com.dotorg.api.utils;

import com.dotorg.api.exceptions.InvalidParameterException;
import com.dotorg.api.objects.Group;
import com.dotorg.api.objects.User;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.tasks.Task;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * dotOrg-api
 * Date Created: 6/15/2016
 * |
 * Original Package: com.dotorg.api.utils
 * |
 * COPYRIGHT 2016
 */
public class ValidationHelper {

    /**
     * Attempt to validate new {@code group} for creation
     *
     * @param group is group to validate, cannot be null
     * @throws InvalidParameterException thrown if the group is invalid
     */
    public static void validateNewGroup(Group group) throws InvalidParameterException {
        // Name is not null
        if (group.getName() == null) {
            throw new InvalidParameterException("Group name cannot be empty.");
        }

        // Name has length > 0
        if (group.getName().length() == 0) {
            throw new InvalidParameterException("Group name must contain at least 1 character.");
        }

        // Name has length <= 140
        if (group.getName().length() > 140) {
            throw new InvalidParameterException("Group name contains more than 140 characters.");
        }

        // Description is null set to ""
        if (group.getDescription() != null && group.getDescription().length() > 255) {
            throw new InvalidParameterException("Group description contains more than 255 characters.");
        }

        // Image url TODO: Validation

    }

    public static void validateUserInGroup(User user, Long groupId) throws UnauthorizedException {
        // Validate user is in group, if not throw unauthorized
        if (!user.getGroups().contains(groupId)) {
            throw new UnauthorizedException("Authorized user does not have access to this group.");
        }
    }

    public static void validateUserNotInGroup(User user, Long groupId) throws BadRequestException {
        // Validate user is in group, if yes throw unauthorized
        if (user.getGroups().contains(groupId)) {
            throw new BadRequestException("Authorized user is already in this group.");
        }
    }

    public static void validateUserWasInGroup(User user, Long groupId) throws UnauthorizedException {
        // Validate user was in group, if not throw unauthorized
        if (!user.getPreviousGroups().contains(groupId)) {
            throw new UnauthorizedException("Authorized user does not have access to this group.");
        }
    }

    public static void validateUserAsGroupOwner(User user, Group group) throws UnauthorizedException {
        // Validate user is creator of group
        if (!group.getCreator().equals(user.getUserId())) {
            throw new UnauthorizedException("Authorized user is not the owner of this group.");
        }
    }

    /**
     * @param token
     * @return
     * @throws NotFoundException
     * @throws UnauthorizedException
     */
    public static User validateToken(String token) throws NotFoundException, UnauthorizedException {
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
            throw new NotFoundException("User not found for Token.");
        }
        return user;
    }
}

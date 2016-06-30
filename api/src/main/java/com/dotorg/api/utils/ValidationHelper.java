package com.dotorg.api.utils;

import com.dotorg.api.exceptions.InvalidParameterException;
import com.dotorg.api.objects.Chat;
import com.dotorg.api.objects.ChatMembership;
import com.dotorg.api.objects.Choice;
import com.dotorg.api.objects.Event;
import com.dotorg.api.objects.Group;
import com.dotorg.api.objects.Member;
import com.dotorg.api.objects.Membership;
import com.dotorg.api.objects.Message;
import com.dotorg.api.objects.News;
import com.dotorg.api.objects.Poll;
import com.dotorg.api.objects.Speaker;
import com.dotorg.api.objects.User;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.tasks.Task;
import com.googlecode.objectify.Key;

import java.util.Date;
import java.util.Objects;

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

    public static void validateNewPoll(Poll poll) throws InvalidParameterException {
        // Title not empty
        if (poll.getTitle() == null) {
            throw new InvalidParameterException("Poll title cannot be empty.");
        }

        // Title longer than >0
        if (poll.getTitle().length() == 0) {
            throw new InvalidParameterException("Poll title must contain at least 1 character.");
        }

        // Title length < 140
        if (poll.getTitle().length() > 140) {
            throw new InvalidParameterException("Poll title contains more than 140 characters.");
        }

        // Description is null set to ""
        if (poll.getDescription() != null && poll.getDescription().length() > 255) {
            throw new InvalidParameterException("Poll description contains more than 255 characters.");
        }

        for (int i = 0; i < poll.getChoices().size(); i++) {
            Choice choice = poll.getChoices().get(i);
            if (choice == null || Objects.equals(choice.getText(), "")) {
                poll.getChoices().remove(i);
                i--;
                continue;
            }
            if (choice.getText().length() > 140) {
                throw new InvalidParameterException("Poll option " + (i + 1) + " contains more than 140 characters.");
            }
        }
    }

    public static void validateNewEvent(Event event) throws InvalidParameterException {
        // Title not empty
        if (event.getTitle() == null) {
            throw new InvalidParameterException("Event title cannot be empty.");
        }

        // Title longer than >0
        if (event.getTitle().length() == 0) {
            throw new InvalidParameterException("Event title must contain at least 1 character.");
        }

        // Title length < 140
        if (event.getTitle().length() > 140) {
            throw new InvalidParameterException("Event title contains more than 140 characters.");
        }

        // Description is null set to ""
        if (event.getDescription() != null && event.getDescription().length() > 255) {
            throw new InvalidParameterException("Event description contains more than 255 characters.");
        }

        // If end date is null
        if (event.getEndDate() == null) {
            throw new InvalidParameterException("Event end date is not valid.");
        }

        // If start date is null
        if (event.getStartDate() == null) {
            throw new InvalidParameterException("Event start date is not valid.");
        }

        // Switch dates if needed
        if (event.getEndDate().before(event.getStartDate())) {
            Date date = event.getEndDate();
            event.setEndDate(event.getStartDate());
            event.setStartDate(date);
        }
    }

    public static void validateNewNews(News news) throws InvalidParameterException {
        // Title not empty
        if (news.getTitle() == null) {
            throw new InvalidParameterException("Event title cannot be empty.");
        }

        // Title longer than >0
        if (news.getTitle().length() == 0) {
            throw new InvalidParameterException("Event title must contain at least 1 character.");
        }

        // Title length < 140
        if (news.getTitle().length() > 140) {
            throw new InvalidParameterException("Event title contains more than 140 characters.");
        }

        // Description is null set to ""
        if (news.getDescription() != null && news.getDescription().length() > 255) {
            throw new InvalidParameterException("Event description contains more than 255 characters.");
        }
    }

    public static void validateUserInGroup(User user, Long groupId) throws UnauthorizedException {
        // Validate user is in group, if not throw unauthorized
        Membership membership = ofy().load().type(Membership.class).ancestor(user).filter("groupId", groupId).first().now();
        if (membership == null || membership.isPrevious()) {
            throw new UnauthorizedException("Authorized user does not have access to this group.");
        }
    }

    public static void validateUserNotInGroup(User user, Long groupId) throws BadRequestException {
        // Validate user is in group, if yes throw unauthorized
        Membership membership = ofy().load().type(Membership.class).ancestor(user).filter("groupId", groupId).first().now();
        if (membership != null) {
            throw new BadRequestException("Authorized user is already in this group.");
        }
    }

    public static void validateUserWasInGroup(User user, Long groupId) throws UnauthorizedException {
        // Validate user was in group, if not throw unauthorized
        Membership membership = ofy().load().type(Membership.class).ancestor(user).filter("groupId", groupId).first().now();
        if (membership == null || !membership.isPrevious()) {
            throw new UnauthorizedException("Authorized user does not have access to this group.");
        }
    }

    public static void validateUserIsGroupOwner(User user, Group group) throws UnauthorizedException {
        // Validate user is creator of group
        if (!Objects.equals(group.getCreator(), user.getUserId())) {
            throw new UnauthorizedException("Authorized user is not the owner of this group.");
        }
    }

    public static void validateUserIsNotGroupOwner(User user, Group group) throws UnauthorizedException {
        // Validate user is creator of group
        if (Objects.equals(group.getCreator(), user.getUserId())) {
            throw new UnauthorizedException("Authorized user is the owner of this group.");
        }
    }

    public static void validateUserIsNotGroupOwner(String userId, Group group) throws UnauthorizedException {
        // Validate user is creator of group
        if (Objects.equals(group.getCreator(), userId)) {
            throw new UnauthorizedException("Authorized user is the owner of this group.");
        }
    }

    public static void validateMemberOfGroup(Member member, Long groupId) throws UnauthorizedException {
        // If ids do not match
        if (!Objects.equals(member.getGroupKey(), Key.create(Group.class, groupId))) {
            throw new UnauthorizedException("Member does not belong to group.");
        }
    }

    public static void validateUserIsMember(User user, Member member) throws UnauthorizedException {
        // If the ids do not match up
        if (!Objects.equals(user.getUserId(), member.getUserId())) {
            throw new UnauthorizedException("Authorized user does not match member.");
        }
    }

    public static void validateUserIsChatMembership(User user, ChatMembership chatMembership) throws UnauthorizedException {
        // If the ids do not match
        if (!Objects.equals(user.getKey(), chatMembership.getUserKey())) {
            throw new UnauthorizedException("Authorized user does not match speaker.");
        }
    }

    public static void validateMemberIsChatMembership(Member member, ChatMembership chatMembership) throws UnauthorizedException {
        // If the ids do not match
        if (!Objects.equals(member.getMemberId(), chatMembership.getMemberId())) {
            throw new UnauthorizedException("Authorized member does not match speaker.");
        }
    }

    public static void validateChatOfGroup(Chat chat, Long groupId) throws UnauthorizedException {
        // If chat key does not match group
        if (!Objects.equals(chat.getGroupKey(), Key.create(Group.class, groupId))) {
            throw new UnauthorizedException("Specified chat does not belong to the group.");
        }
    }

    public static void validateMessageOfChat(Message message, Long chatId) throws UnauthorizedException {
        // If ids do not align
        if (!Objects.equals(message.getChatKey(), Key.create(Chat.class, chatId))) {
            throw new UnauthorizedException("Specified message does not belong to the chat.");
        }
    }

    public static void validateSpeakerIsUser(Speaker speaker, User user) throws UnauthorizedException {
        // If speaker Id does not match user
        if (!Objects.equals(speaker.getUserId(), user.getUserId())) {
            throw new UnauthorizedException("Speaker does not match User.");
        }
    }

    public static void validateSpeakerOfChat(Speaker speaker, Long chatId) throws UnauthorizedException {
        // If id do not match
        if (!Objects.equals(speaker.getChatKey(), Key.create(Chat.class, chatId))) {
            throw new UnauthorizedException("Speaker does not belong to specified chat.");
        }
    }

    public static void validatePollOfGroup(Poll poll, Long groupId) throws UnauthorizedException {
        // If poll group and group dont match
        if (!Objects.equals(poll.getGroupKey(), Key.create(Group.class, groupId))) {
            throw new UnauthorizedException("Poll does not belong to specified group.");
        }
    }

    public static void validateChoiceOfPoll(Choice choice, Poll poll) throws UnauthorizedException {
        // If choice and poll do not match
        if (!Objects.equals(choice.getPollKey(), poll.getKey())) {
            throw new UnauthorizedException("Choice does not belong to specified poll.");
        }
    }

    public static void validateCreatorOfPoll(Poll poll, Member member) throws UnauthorizedException {
        // If ids do not match
        if (!Objects.equals(poll.getMemberId(), member.getMemberId())) {
            throw new UnauthorizedException("Member is not the creator of the poll.");
        }
    }

    public static void validateEventOfGroup(Event event, Long groupId) throws UnauthorizedException {
        // If event group and group dont match
        if (!Objects.equals(event.getGroupKey(), Key.create(Group.class, groupId))) {
            throw new UnauthorizedException("Event does not belong to specified group.");
        }
    }

    public static void validateCreatorOfEvent(Event event, Member member) throws UnauthorizedException {
        // If ids do not match
        if (!Objects.equals(event.getMemberId(), member.getMemberId())) {
            throw new UnauthorizedException("Member is not the creator of the event.");
        }
    }

    public static void validateNewsOfGroup(News news, Long groupId) throws UnauthorizedException {
        // If news group and group dont match
        if (!Objects.equals(news.getGroupKey(), Key.create(Group.class, groupId))) {
            throw new UnauthorizedException("News does not belong to specified group.");
        }
    }

    public static void validateCreatorOfNews(News news, Member member) throws UnauthorizedException {
        // If ids do not match
        if (!Objects.equals(news.getMemberId(), member.getMemberId())) {
            throw new UnauthorizedException("Member is not the creator of the news.");
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

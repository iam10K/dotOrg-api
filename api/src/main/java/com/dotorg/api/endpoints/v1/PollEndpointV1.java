package com.dotorg.api.endpoints.v1;

import com.dotorg.api.exceptions.InvalidParameterException;
import com.dotorg.api.objects.Choice;
import com.dotorg.api.objects.Group;
import com.dotorg.api.objects.Member;
import com.dotorg.api.objects.Membership;
import com.dotorg.api.objects.Poll;
import com.dotorg.api.objects.User;
import com.dotorg.api.objects.Vote;
import com.dotorg.api.utils.ValidationHelper;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

@ApiReference(BaseEndpointV1.class)
public class PollEndpointV1 {

    private static final Logger logger = Logger.getLogger(PollEndpointV1.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Inserts a new {@code Poll}.
     */
    @ApiMethod(
            name = "groups.polls.create",
            path = "groups/{groupId}/polls",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Poll create(Poll poll, @Named("groupId") Long groupId, @Named("token") String token) throws UnauthorizedException, NotFoundException, InvalidParameterException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        Membership membership = ofy().load().type(Membership.class).ancestor(user).filter("groupId", groupId).first().now();
        if (membership == null) {
            throw new NotFoundException("Could not find Membership for group ID-" + groupId + " and user ID-" + user.getUserId());
        }

        Member member = ofy().load().key(membership.getMemberKey()).now();
        if (member == null) {
            throw new NotFoundException("Could not find Member with ID: " + membership.getMemberId());
        }

        // FUTURE Permissions

        ValidationHelper.validateNewPoll(poll);

        return createPoll(poll, user, member);
    }

    /**
     * Updates an existing {@code Poll}.
     *
     * @param pollId  the ID of the entity to be updated
     * @param newPoll the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code pollId} does not correspond to an existing
     *                           {@code Poll}
     */
    @ApiMethod(
            name = "groups.polls.update",
            path = "groups/{groupId}/polls/{pollId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Poll update(Poll newPoll, @Named("groupId") Long groupId, @Named("pollId") Long pollId, @Named("token") String token) throws NotFoundException, UnauthorizedException, InvalidParameterException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        Membership membership = ofy().load().type(Membership.class).ancestor(user).filter("groupId", groupId).first().now();
        if (membership == null) {
            throw new NotFoundException("Could not find Membership for group ID-" + groupId + " and user ID-" + user.getUserId());
        }

        Member member = ofy().load().key(membership.getMemberKey()).now();
        if (member == null) {
            throw new NotFoundException("Could not find Member with ID: " + membership.getMemberId());
        }

        Poll poll = ofy().load().type(Poll.class).parent(Key.create(Group.class, groupId)).id(pollId).now();
        if (poll == null) {
            throw new NotFoundException("Could not find poll with ID: " + pollId);
        }

        ValidationHelper.validatePollOfGroup(poll, groupId);

        // FUTURE Permission
        // If poll creator allow delete
        ValidationHelper.validateCreatorOfPoll(poll, member);

        ValidationHelper.validateNewPoll(newPoll);

        return updatePoll(poll, newPoll);
    }

    /**
     * Deletes the specified {@code Poll}.
     *
     * @param pollId the ID of the entity to delete
     * @throws NotFoundException if the {@code pollId} does not correspond to an existing
     *                           {@code Poll}
     */
    @ApiMethod(
            name = "groups.polls.delete",
            path = "groups/{groupId}/polls/{pollId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void delete(@Named("groupId") Long groupId, @Named("pollId") Long pollId, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        Membership membership = ofy().load().type(Membership.class).ancestor(user).filter("groupId", groupId).first().now();
        if (membership == null) {
            throw new NotFoundException("Could not find Membership for group ID-" + groupId + " and user ID-" + user.getUserId());
        }

        Member member = ofy().load().key(membership.getMemberKey()).now();
        if (member == null) {
            throw new NotFoundException("Could not find Member with ID: " + membership.getMemberId());
        }

        Poll poll = ofy().load().type(Poll.class).parent(Key.create(Group.class, groupId)).id(pollId).now();
        if (poll == null) {
            throw new NotFoundException("Could not find poll with ID: " + pollId);
        }

        ValidationHelper.validatePollOfGroup(poll, groupId);

        // FUTURE Permission
        // If poll creator allow delete
        ValidationHelper.validateCreatorOfPoll(poll, member);

        ofy().delete().type(Choice.class).parent(poll);
        ofy().delete().type(Vote.class).parent(poll);
        ofy().delete().entity(poll).now();
        logger.info("Deleted Poll with ID: " + pollId);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "groups.polls.list",
            path = "groups/{groupId}/polls",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Poll> list(@Named("groupId") Long groupId, @Named("token") String token, @Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        Group group = ofy().load().type(Group.class).id(groupId).now();
        if (group == null) {
            throw new NotFoundException("Could not find group with ID: " + groupId);
        }

        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Poll> query = ofy().load().type(Poll.class).ancestor(group).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Poll> queryIterator = query.iterator();
        List<Poll> pollList = new ArrayList<Poll>(limit);
        while (queryIterator.hasNext()) {
            pollList.add(queryIterator.next());
        }
        return CollectionResponse.<Poll>builder().setItems(pollList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }


    /**
     * Vote in the specified {@code Poll}.
     *
     * @param pollId   the ID of the entity to delete
     * @param choiceId
     * @throws NotFoundException if the {@code pollId} does not correspond to an existing
     *                           {@code Poll}
     */
    @ApiMethod(
            name = "groups.polls.vote",
            path = "groups/{groupId}/polls/{pollId}/vote/{choiceId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Vote vote(@Named("groupId") Long groupId, @Named("pollId") Long pollId, @Named("choiceId") Long choiceId, @Named("token") String token) throws NotFoundException, UnauthorizedException, ForbiddenException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        Membership membership = ofy().load().type(Membership.class).ancestor(user).filter("groupId", groupId).first().now();
        if (membership == null) {
            throw new NotFoundException("Could not find Membership for group ID-" + groupId + " and user ID-" + user.getUserId());
        }

        Member member = ofy().load().key(membership.getMemberKey()).now();
        if (member == null) {
            throw new NotFoundException("Could not find Member with ID: " + membership.getMemberId());
        }

        Poll poll = ofy().load().type(Poll.class).parent(Key.create(Group.class, groupId)).id(pollId).now();
        if (poll == null) {
            throw new NotFoundException("Could not find poll with ID: " + pollId);
        }

        ValidationHelper.validatePollOfGroup(poll, groupId);

        if (poll.getEndDate() != null && new Date().getTime() > poll.getEndDate().getTime()) {
            throw new ForbiddenException("Cannot vote on a poll that has ended.");
        }

        Choice choice = ofy().load().type(Choice.class).parent(poll).id(choiceId).now();
        if (choice == null) {
            throw new NotFoundException("Could not find choice with ID: " + choiceId);
        }

        ValidationHelper.validateChoiceOfPoll(choice, poll);

        Vote vote = ofy().load().type(Vote.class).ancestor(poll).filter("userId", user.getUserId()).first().now();
        if (vote == null) {
            vote = new Vote(poll.getKey(), member, user, choiceId);
            Key<Vote> voteKey = ofy().save().entity(vote).now();
            return ofy().load().key(voteKey).now();
        } else {
            vote.setChoiceId(choiceId);
            ofy().save().entity(vote).now();
            return vote;
        }
    }

    private void checkExists(Long pollId) throws NotFoundException {
        try {
            ofy().load().type(Poll.class).id(pollId).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Poll with ID: " + pollId);
        }
    }

    private void loadPoll(Poll poll) {
        List<Choice> choices = ofy().load().type(Choice.class).ancestor(poll).list();
        poll.setChoices(choices);
        List<Vote> votes = ofy().load().type(Vote.class).ancestor(poll).list();
        poll.setVoteList(votes);
    }

    private Poll createPoll(Poll poll, User user, Member member) {
        poll.createNewPoll(member, user, member.getGroupKey());

        Key<Poll> pollKey = ofy().save().entity(poll).now();

        for (Choice choice : poll.getChoices()) {
            choice.createNewChoice(pollKey);
            ofy().save().entity(choice).now();
        }

        poll = ofy().load().key(pollKey).now();
        logger.info("Created Poll with ID: " + poll.getPollId());

        loadPoll(poll);

        return poll;
    }

    private Poll updatePoll(Poll poll, Poll newPoll) {
        poll.setTitle(newPoll.getTitle());
        poll.setDescription(newPoll.getDescription());
        poll.setEndDate(newPoll.getEndDate());
        poll.setAnonymous(newPoll.isAnonymous());

        for (Choice choice : poll.getChoices()) {
            Choice existingChoice = ofy().load().type(Choice.class).id(choice.getChoiceId()).now();
            if (existingChoice != null) {
                choice.setText(choice.getText());
            } else {
                choice.createNewChoice(poll.getKey());
            }
            ofy().save().entity(choice).now();
        }

        logger.info("Updated Poll with ID: " + poll.getPollId());

        loadPoll(poll);

        return poll;
    }
}
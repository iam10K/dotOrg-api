package com.dotorg.api.endpoints.v1;

import com.dotorg.api.exceptions.InvalidParameterException;
import com.dotorg.api.objects.Event;
import com.dotorg.api.objects.Group;
import com.dotorg.api.objects.Member;
import com.dotorg.api.objects.Membership;
import com.dotorg.api.objects.User;
import com.dotorg.api.utils.ValidationHelper;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

@ApiReference(BaseEndpointV1.class)
public class EventEndpointV1 {

    private static final Logger logger = Logger.getLogger(EventEndpointV1.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Inserts a new {@code Event}.
     */
    @ApiMethod(
            name = "groups.events.create",
            path = "groups/{groupId}/events",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Event create(Event event, @Named("groupId") Long groupId, @Named("token") String token) throws UnauthorizedException, NotFoundException, InvalidParameterException {
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
        //  FUTURE Permission

        ValidationHelper.validateNewEvent(event);

        return createEvent(event, groupId, user, member);
    }

    /**
     * Updates an existing {@code Event}.
     *
     * @param eventId  the ID of the entity to be updated
     * @param newEvent the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code eventId} does not correspond to an existing
     *                           {@code Event}
     */
    @ApiMethod(
            name = "groups.events.update",
            path = "groups/{groupId}/events/{eventId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Event update(Event newEvent, @Named("groupId") Long groupId, @Named("eventId") Long eventId, @Named("token") String token) throws NotFoundException, UnauthorizedException, InvalidParameterException {
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

        Event event = ofy().load().type(Event.class).parent(Key.create(Group.class, groupId)).id(eventId).now();
        if (event == null) {
            throw new NotFoundException("Could not find event with ID: " + eventId);
        }

        ValidationHelper.validateEventOfGroup(event, groupId);

        // FUTURE Permission to update
        // If event creator allow update
        ValidationHelper.validateCreatorOfEvent(event, member);

        ValidationHelper.validateNewEvent(event);

        return updateEvent(event, newEvent);
    }

    /**
     * Deletes the specified {@code Event}.
     *
     * @param eventId the ID of the entity to delete
     * @throws NotFoundException if the {@code eventId} does not correspond to an existing
     *                           {@code Event}
     */
    @ApiMethod(
            name = "groups.events.delete",
            path = "groups/{groupId}/events/{eventId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void delete(@Named("eventId") Long eventId, @Named("groupId") Long groupId, @Named("token") String token) throws NotFoundException, UnauthorizedException {
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

        Event event = ofy().load().type(Event.class).parent(Key.create(Group.class, groupId)).id(eventId).now();
        if (event == null) {
            throw new NotFoundException("Could not find event with ID: " + eventId);
        }

        ValidationHelper.validateEventOfGroup(event, groupId);

        // FUTURE Permission to delete
        // If event creator allow delete
        ValidationHelper.validateCreatorOfEvent(event, member);

        ofy().delete().entity(event).now();
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "groups.events.list",
            path = "groups/{groupId}/events",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Event> list(@Named("groupId") Long groupId, @Named("token") String token, @Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        Group group = ofy().load().type(Group.class).id(groupId).now();
        if (group == null) {
            throw new NotFoundException("Could not find group with ID: " + groupId);
        }

        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Event> query = ofy().load().type(Event.class).ancestor(group).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Event> queryIterator = query.iterator();
        List<Event> eventList = new ArrayList<Event>(limit);
        while (queryIterator.hasNext()) {
            eventList.add(queryIterator.next());
        }
        return CollectionResponse.<Event>builder().setItems(eventList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long eventId) throws NotFoundException {
        try {
            ofy().load().type(Event.class).id(eventId).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Event with ID: " + eventId);
        }
    }

    private Event createEvent(Event event, Long groupId, User user, Member member) {
        event.createNewEvent(member, user, Key.create(Group.class, groupId));

        Key<Event> eventKey = ofy().save().entity(event).now();

        event = ofy().load().key(eventKey).now();
        logger.info("Created Event with ID: " + event.getEventId());
        return event;
    }

    private Event updateEvent(Event event, Event newEvent) {
        event.setStartDate(newEvent.getStartDate());
        event.setEndDate(newEvent.getEndDate());
        event.setTitle(newEvent.getTitle());
        event.setDescription(newEvent.getDescription());
        event.setLocation(newEvent.getLocation());

        ofy().save().entity(event).now();
        return event;
    }
}
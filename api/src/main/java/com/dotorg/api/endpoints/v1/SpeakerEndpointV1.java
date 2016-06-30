package com.dotorg.api.endpoints.v1;

import com.dotorg.api.objects.ChatMembership;
import com.dotorg.api.objects.Member;
import com.dotorg.api.objects.Speaker;
import com.dotorg.api.objects.User;
import com.dotorg.api.utils.ValidationHelper;
import com.dotorg.api.wrappers.MembersWrapper;
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
public class SpeakerEndpointV1 {

    private static final Logger logger = Logger.getLogger(SpeakerEndpointV1.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Inserts a new {@code Speaker}.
     */
    @ApiMethod(
            name = "groups.chats.speakers.add",
            path = "groups/{groupId}/chats/{chatId}/speakers",
            httpMethod = ApiMethod.HttpMethod.POST)
    public CollectionResponse<Speaker> add(MembersWrapper membersWrapper, @Named("groupId") Long groupId, @Named("chatId") Long chatId, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        // Validate user in chat
        ChatMembership chatMembership = ofy().load().type(ChatMembership.class).ancestor(user).filter("chatId", chatId).first().now();
        if (chatMembership == null) {
            throw new UnauthorizedException("Authorized user is not a member of specified chat.");
        }
        // FUTURE: Permission to add members to chat

        List<Speaker> speakers = new ArrayList<>();
        addSpeakers(groupId, chatMembership, membersWrapper, speakers);

        // FUTURE: Notification / push message to chat
        return CollectionResponse.<Speaker>builder().setItems(speakers).build();
    }

    /**
     * Updates an existing {@code Speaker}.
     *
     * @param speakerId the ID of the entity to be updated
     * @param newSpeaker   the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code speakerId} does not correspond to an existing
     *                           {@code Speaker}
     */
    @ApiMethod(
            name = "groups.chats.speakers.update",
            path = "groups/{groupId}/chats/{chatId}/speakers/{speakerId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Speaker update(Speaker newSpeaker, @Named("groupId") Long groupId, @Named("chatId") Long chatId, @Named("speakerId") Long speakerId, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        Speaker speaker = ofy().load().type(Speaker.class).id(speakerId).now();
        if (speaker == null) {
            throw new NotFoundException("Speaker not found with ID: " + speakerId);
        }

        ValidationHelper.validateSpeakerIsUser(speaker, user);
        ValidationHelper.validateSpeakerOfChat(speaker, chatId);

        return updateSpeaker(speaker, newSpeaker);
    }

    /**
     * Deletes the specified {@code Speaker}.
     *
     * @param speakerId the ID of the entity to delete
     * @throws NotFoundException if the {@code speakerId} does not correspond to an existing
     *                           {@code Speaker}
     */
    @ApiMethod(
            name = "groups.chats.speakers.remove",
            path = "groups/{groupId}/chats/{chatId}/speakers/{speakerId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("groupId") Long groupId, @Named("chatId") Long chatId, @Named("speakerId") Long speakerId, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        // Validate user in chat
        ChatMembership chatMembership = ofy().load().type(ChatMembership.class).ancestor(user).filter("chatId", chatId).first().now();
        if (chatMembership == null) {
            throw new UnauthorizedException("Authorized user is not a member of specified chat.");
        }

        // FUTURE: Permissions to remove

        Speaker speaker = ofy().load().type(Speaker.class).id(speakerId).now();
        if (speaker == null) {
            throw new NotFoundException("Speaker not found with ID: " + speakerId);
        }

        ChatMembership speakerMembership = ofy().load().key(speaker.getChatMembershipKey()).now();
        if (speakerMembership == null) {
            throw new NotFoundException("Speaker not found with ID: " + speakerId);
        }

        // FUTURE: able to rejoin chat?( by adding leaving or kicking)

        ofy().delete().entity(speakerMembership).now();
        ofy().delete().entity(speaker).now();
        logger.info("Deleted Speaker with ID: " + speakerId);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "groups.chats.speakers.list",
            path = "groups/{groupId}/chats/{chatId}/speakers",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Speaker> list(@Named("groupId") Long groupId, @Named("chatId") Long chatId, @Named("token") String token, @Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Speaker> query = ofy().load().type(Speaker.class).filter("chatId", chatId).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Speaker> queryIterator = query.iterator();
        List<Speaker> speakerList = new ArrayList<>(limit);
        while (queryIterator.hasNext()) {
            speakerList.add(queryIterator.next());
        }
        return CollectionResponse.<Speaker>builder().setItems(speakerList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void addSpeakers(Long groupId, ChatMembership chatMembership, MembersWrapper membersWrapper, List<Speaker> speakers) {
        for (Member wrapperMember : membersWrapper.getMemberList()) {
            if (wrapperMember.getMemberId() != null) {
                Member member = ofy().load().key(wrapperMember.getKey()).now();
                if (member == null) {
                    // No member found
                    continue;
                }

                try {
                    ValidationHelper.validateMemberOfGroup(member, groupId);
                } catch (UnauthorizedException ex) {
                    // Member is not in group
                    continue;
                }

                ChatMembership checkChatMembership = ofy().load().type(ChatMembership.class).ancestor(member.getUserKey()).filter("chatId", chatMembership.getChatId()).first().now();
                if (checkChatMembership != null) {
                    // Member already in chat
                    continue;
                }

                Speaker speaker = new Speaker(member.getMemberId(), chatMembership.getChatKey());
                Key<Speaker> speakerKey = ofy().save().entity(speaker).now();
                speaker = ofy().load().key(speakerKey).now();

                ChatMembership newChatMembership = new ChatMembership(member.getUserKey(), chatMembership.getChatId(), groupId, speaker.getSpeakerId(), member.getMemberId());
                ofy().save().entity(newChatMembership).now();

                speakers.add(speaker);

                // FUTURE: Add notification (admin message)
            }
        }
    }

    private Speaker updateSpeaker(Speaker speaker, Speaker newSpeaker) {
        speaker.setMuted(newSpeaker.isMuted());

        ofy().save().entity(speaker).now();
        logger.info("Updated Speaker: " + speaker.getSpeakerId());
        return speaker;
    }
}
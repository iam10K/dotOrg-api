package com.dotorg.api.endpoints.v1;

import com.dotorg.api.exceptions.InvalidParameterException;
import com.dotorg.api.objects.Chat;
import com.dotorg.api.objects.ChatMembership;
import com.dotorg.api.objects.Member;
import com.dotorg.api.objects.Message;
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
import java.util.Objects;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

@ApiReference(BaseEndpointV1.class)
public class MessageEndpointV1 {

    private static final Logger logger = Logger.getLogger(MessageEndpointV1.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Inserts a new {@code Message}.
     */
    @ApiMethod(
            name = "groups.chats.messages.create",
            path = "groups/{groupId}/chats/{chatId}/messages",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Message create(Message message, @Named("chatId") Long chatId, @Named("groupId") Long groupId, @Named("token") String token) throws NotFoundException, UnauthorizedException, InvalidParameterException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        // Validate user in chat
        ChatMembership chatMembership = ofy().load().type(ChatMembership.class).ancestor(user).filter("chatId", chatId).first().now();
        if (chatMembership == null) {
            throw new UnauthorizedException("Authorized user is not a member of specified chat.");
        }

        Chat chat = ofy().load().key(chatMembership.getChatKey()).now();
        if (chat == null) {
            throw new NotFoundException("Could not find Chat with ID: " + chatId);
        }

        ValidationHelper.validateChatOfGroup(chat, groupId);

        Member member = ofy().load().key(chatMembership.getMemberKey()).now();
        if (member == null) {
            throw new NotFoundException("Could not find Member with ID: " + chatMembership.getMemberId());
        }

        return createMessage(user, member, chat, message);
    }

    /**
     * Deletes the specified {@code Message}.
     *
     * @param messageId the ID of the entity to delete
     * @throws NotFoundException if the {@code messageId} does not correspond to an existing
     *                           {@code Message}
     */
    @ApiMethod(
            name = "groups.chats.messages.delete",
            path = "groups/{groupId}/chats/{chatId}/messages/{messageId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void delete(@Named("messageId") Long messageId, @Named("chatId") Long chatId, @Named("groupId") Long groupId, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        // Validate user in chat
        ChatMembership chatMembership = ofy().load().type(ChatMembership.class).ancestor(user).filter("chatId", chatId).first().now();
        if (chatMembership == null) {
            throw new UnauthorizedException("Authorized user is not a member of specified chat.");
        }

        Chat chat = ofy().load().key(chatMembership.getChatKey()).now();
        if (chat == null) {
            throw new NotFoundException("Could not find Chat with ID: " + chatId);
        }

        Message message = ofy().load().type(Message.class).id(messageId).now();
        if (message == null) {
            throw new NotFoundException("Specified message does not exist.");
        }

        ValidationHelper.validateChatOfGroup(chat, groupId);
        ValidationHelper.validateMessageOfChat(message, chatId);

        // FUTURE: Permission to delete

        ofy().delete().entity(message).now();
        logger.info("Deleted Message with ID: " + messageId);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "groups.chats.messages.list",
            path = "groups/{groupId}/chats/{chatId}/messages",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Message> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit, @Named("chatId") Long chatId, @Named("groupId") Long groupId, @Named("token") String token) throws UnauthorizedException, NotFoundException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        // Validate user in chat
        ChatMembership chatMembership = ofy().load().type(ChatMembership.class).ancestor(user).filter("chatId", chatId).first().now();
        if (chatMembership == null) {
            throw new UnauthorizedException("Authorized user is not a member of specified chat.");
        }

        Chat chat = ofy().load().key(chatMembership.getChatKey()).now();
        if (chat == null) {
            throw new NotFoundException("Could not find Chat with ID: " + chatId);
        }

        ValidationHelper.validateChatOfGroup(chat, groupId);

        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Message> query = ofy().load().type(Message.class).ancestor(chat.getKey()).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Message> queryIterator = query.iterator();
        List<Message> messageList = new ArrayList<>(limit);
        while (queryIterator.hasNext()) {
            messageList.add(queryIterator.next());
        }
        return CollectionResponse.<Message>builder().setItems(messageList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private Message createMessage(User user, Member member, Chat chat, Message message) throws InvalidParameterException {
        if (Objects.equals(message.getText(), "")) {
            throw new InvalidParameterException("Message must be longer than 0 characters.");
        } else if (message.getText().length() > 1000) {
            throw new InvalidParameterException("Message must be less than 1,000 characters.");
        }

        // TODO: Format attachments

        Message newMessage = new Message(chat, user, member, message.getText(), message.getAttachments());

        Key<Message> messageKey = ofy().save().entity(newMessage).now();
        logger.info("Created Message with ID: " + newMessage.getMessageId());

        return ofy().load().key(messageKey).now();
    }
}
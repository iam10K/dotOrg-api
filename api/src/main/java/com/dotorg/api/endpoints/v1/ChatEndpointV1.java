package com.dotorg.api.endpoints.v1;

import com.dotorg.api.exceptions.InvalidParameterException;
import com.dotorg.api.objects.Chat;
import com.dotorg.api.objects.ChatMembership;
import com.dotorg.api.objects.Chatter;
import com.dotorg.api.objects.Group;
import com.dotorg.api.objects.Message;
import com.dotorg.api.objects.User;
import com.dotorg.api.utils.ValidationHelper;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

@ApiReference(BaseEndpointV1.class)
public class ChatEndpointV1 {

    private static final Logger logger = Logger.getLogger(ChatEndpointV1.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Returns the {@link Chat} with the corresponding ID.
     *
     * @param chatId the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Chat} with the provided ID.
     */
    @ApiMethod(
            name = "groups.chats.get",
            path = "groups/{groupId}/chats/{chatId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Chat get(@Named("chatId") Long chatId, @Named("groupId") Long groupId, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        // Validate user in chat
        ChatMembership chatMembership = ofy().load().type(ChatMembership.class).ancestor(user).filter("chatId", chatId).first().now();
        if (chatMembership == null) {
            throw new UnauthorizedException("Authorized user is not a member of specified chat.");
        }

        logger.info("Getting Chat with ID: " + chatId);
        Chat chat = ofy().load().type(Chat.class).id(chatId).now();
        if (chat == null) {
            throw new NotFoundException("Could not find Chat with ID: " + chatId);
        }
        return chat;
    }

    /**
     * Inserts a new {@code Chat}.
     */
    @ApiMethod(
            name = "groups.chats.create",
            path = "groups/{groupId}/chats",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Chat create(Chat chat, @Named("groupId") Long groupId, @Named("token") String token) throws NotFoundException, UnauthorizedException, InvalidParameterException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        return createChat(chat, groupId, user);
    }

    /**
     * Updates an existing {@code Chat}.
     *
     * @param chatId  the ID of the entity to be updated
     * @param newChat the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code chatId} does not correspond to an existing
     *                           {@code Chat}
     */
    @ApiMethod(
            name = "groups.chats.update",
            path = "groups/{groupId}/chats/{chatId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Chat update(Chat newChat, @Named("groupId") Long groupId, @Named("chatId") Long chatId, @Named("token") String token) throws NotFoundException, UnauthorizedException, InvalidParameterException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        // Validate user in chat
        ChatMembership chatMembership = ofy().load().type(ChatMembership.class).ancestor(user).filter("chatId", chatId).first().now();
        if (chatMembership == null) {
            throw new UnauthorizedException("Authorized user is not a member of specified chat.");
        }

        Chat chat = ofy().load().type(Chat.class).id(chatId).now();
        if (chat == null) {
            throw new NotFoundException("Could not find Chat with ID: " + chatId);
        }

        // TODO: Validate chat belongs to group Validation helper

        // FUTURE: Permission to rename chat?
        return updateChat(chat, newChat);
    }

    /**
     * Deletes the specified {@code Chat}.
     *
     * @param chatId the ID of the entity to delete
     * @throws NotFoundException if the {@code chatId} does not correspond to an existing
     *                           {@code Chat}
     */
    @ApiMethod(
            name = "groups.chats.delete",
            path = "groups/{groupId}/chats/{chatId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void delete(@Named("groupId") Long groupId, @Named("chatId") Long chatId, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        // Validate user in chat
        ChatMembership chatMembership = ofy().load().type(ChatMembership.class).ancestor(user).filter("chatId", chatId).first().now();
        if (chatMembership == null) {
            throw new UnauthorizedException("Authorized user is not a member of specified chat.");
        }

        Chat chat = ofy().load().type(Chat.class).id(chatId).now();
        if (chat == null) {
            throw new NotFoundException("Could not find Chat with ID: " + chatId);
        }

        ValidationHelper.validateChatOfGroup(chat, groupId);

        // FUTURE: Permissions to remove

        removeChat(chat);
        logger.info(user.getUserId() + ", Deleted chat with ID: " + chatId);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "groups.chats.list",
            path = "groups/{groupId}/chats",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Chat> list(@Named("groupId") Long groupId, @Nullable @Named("cursor") Integer cursor, @Nullable @Named("limit") Integer limit, @Named("token") String token) throws NotFoundException, UnauthorizedException {
        User user = ValidationHelper.validateToken(token);

        ValidationHelper.validateUserInGroup(user, groupId);

        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        cursor = cursor == null ? 0 : cursor;

        List<Chat> chats = new ArrayList<>();
        List<ChatMembership> chatMembershipList = ofy().load().type(ChatMembership.class).ancestor(user).filter("groupId", groupId).list();
        for (int i = cursor; i < chatMembershipList.size() && i < limit; i++) {
            Chat chat = ofy().load().type(Chat.class).id(chatMembershipList.get(i).getChatId()).now();
            if (chat != null) {
                loadChat(chat);
                chats.add(chat);
            }
        }

        return CollectionResponse.<Chat>builder().setItems(chats).build();
    }

    private void loadChat(Chat chat) {
        // TODO Currently here

    }

    private Chat createChat(Chat chat, Long groupId, User user) throws InvalidParameterException {
        if (chat.getName() == null || chat.getName().equals("")) {
            throw new InvalidParameterException("Invalid chat name, must be longer.");
        }

        Chat newChat = new Chat(chat.getName(), Key.create(Group.class, groupId));

        Key<Chat> chatKey = ofy().save().entity(newChat).now();
        logger.info("Created Chat");
        return ofy().load().key(chatKey).now();
    }

    private Chat updateChat(Chat chat, Chat newChat) {
        if (!Objects.equals(newChat.getName(), "")) {
            chat.setName(newChat.getName());
            // FUTURE: push notification/ add message
        }

        if (!Objects.equals(newChat.getDescription(), "")) {
            chat.setDescription(newChat.getDescription());
            // FUTURE: push/add message
        }

        ofy().save().entity(chat).now();
        logger.info("Updated Chat: " + chat.getChatId());
        return chat;
    }

    private void removeChat(Chat chat) {
        // Delete all chatMemberships, Chatters, and Messages
        List<ChatMembership> chatMembershipList = ofy().load().type(ChatMembership.class).filter("chatId", chat.getChatId()).list();
        ofy().delete().entities(chatMembershipList).now();
        ofy().delete().type(Chatter.class).parent(chat);
        ofy().delete().type(Message.class).parent(chat);
        ofy().delete().entity(chat);
    }
}
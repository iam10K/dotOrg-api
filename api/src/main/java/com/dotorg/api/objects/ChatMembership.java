package com.dotorg.api.objects;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

/**
 * dotOrg-api
 * Date Created: 6/21/2016
 * |
 * Original Package: com.dotorg.api.objects
 * |
 * COPYRIGHT 2016
 */
public class ChatMembership {

    @Id
    private Long chatMembershipId; // Same as speakerID (allows for easier accessing)

    @Parent
    private Key<User> userKey;

    @Index
    private Long chatId;

    @Index
    private Long groupId;

    private Long memberId;

    public ChatMembership(Key<User> userKey, Long chatId, Long groupId, Long speakerId, Long memberId) {
        this.userKey = userKey;
        this.chatId = chatId;
        this.groupId = groupId;
        this.chatMembershipId = speakerId;
        this.memberId = memberId;
    }

    public Key<ChatMembership> getKey() {
        return Key.create(ChatMembership.class, chatMembershipId);
    }

    public Long getChatMembershipId() {
        return chatMembershipId;
    }

    public void setChatMembershipId(Long chatMembershipId) {
        this.chatMembershipId = chatMembershipId;
    }


    public Key<User> getUserKey() {
        return userKey;
    }

    public void setUserKey(Key<User> userKey) {
        this.userKey = userKey;
    }


    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Key<Chat> getChatKey() {
        return Key.create(Chat.class, chatId);
    }


    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Key<Group> getGroupKey() {
        return Key.create(Group.class, groupId);
    }


    public Long getSpeakerId() {
        return chatMembershipId;
    }

    public Key<Speaker> getChatterKey() {
        return Key.create(Speaker.class, chatMembershipId);
    }


    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Key<Member> getMemberKey() {
        return Key.create(Member.class, memberId);
    }
}

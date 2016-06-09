package com.dotorg.api;

import com.dotorg.api.objects.Chat;
import com.dotorg.api.objects.Chatter;
import com.dotorg.api.objects.Event;
import com.dotorg.api.objects.Group;
import com.dotorg.api.objects.Member;
import com.dotorg.api.objects.Message;
import com.dotorg.api.objects.News;
import com.dotorg.api.objects.Poll;
import com.dotorg.api.objects.User;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class OfyHelper {
    static {
        ObjectifyService.register(Chat.class);
        ObjectifyService.register(Chatter.class);
        ObjectifyService.register(Event.class);
        ObjectifyService.register(Group.class);
        ObjectifyService.register(Member.class);
        ObjectifyService.register(Message.class);
        ObjectifyService.register(News.class);
        ObjectifyService.register(Poll.class);
        ObjectifyService.register(User.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
        //since v.4.0  use ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}

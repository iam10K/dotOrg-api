package com.dotorg.api.utils;

import com.dotorg.api.objects.Chat;
import com.dotorg.api.objects.Chatter;
import com.dotorg.api.objects.Event;
import com.dotorg.api.objects.Group;
import com.dotorg.api.objects.Member;
import com.dotorg.api.objects.Message;
import com.dotorg.api.objects.News;
import com.dotorg.api.objects.Poll;
import com.dotorg.api.objects.User;
import com.googlecode.objectify.ObjectifyService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class OfyHelper implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
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

    public void contextDestroyed(ServletContextEvent event) {
        // App Engine does not currently invoke this method.
    }
}

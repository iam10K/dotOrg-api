package com.dotorg.api.utils;

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
import com.dotorg.api.objects.Vote;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.googlecode.objectify.ObjectifyService;

import java.io.FileInputStream;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class OfyHelper implements ServletContextListener {
    private FirebaseOptions options;

    public OfyHelper() {
        try {
            options = new FirebaseOptions.Builder()
                    .setServiceAccount(new FileInputStream("WEB-INF/firebase/dotOrg-API.json"))
                    .setDatabaseUrl("https://dotorg-api.firebaseio.com/")
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void contextInitialized(ServletContextEvent event) {
        ObjectifyService.register(Chat.class);
        ObjectifyService.register(ChatMembership.class);
        ObjectifyService.register(Choice.class);
        ObjectifyService.register(Event.class);
        ObjectifyService.register(Group.class);
        ObjectifyService.register(Member.class);
        ObjectifyService.register(Membership.class);
        ObjectifyService.register(Message.class);
        ObjectifyService.register(News.class);
        ObjectifyService.register(Poll.class);
        ObjectifyService.register(Speaker.class);
        ObjectifyService.register(User.class);
        ObjectifyService.register(Vote.class);
    }

    public void contextDestroyed(ServletContextEvent event) {
        // App Engine does not currently invoke this method.
    }
}

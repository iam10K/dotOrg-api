package com.dotorg.api.wrappers;

import com.dotorg.api.objects.User;

import java.util.List;

/**
 * dotOrg-api
 * Date Created: 6/18/2016
 * |
 * Original Package: com.dotorg.api.wrappers
 * |
 * COPYRIGHT 2016
 */
public class UsersWrapper {
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}

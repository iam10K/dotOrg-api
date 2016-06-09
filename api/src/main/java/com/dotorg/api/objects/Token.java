package com.dotorg.api.objects;

/**
 * dotOrg-api
 * Date Created: 6/7/2016
 * |
 * Original Package: com.dotorg.api.objects
 * |
 * COPYRIGHT 2016
 */
public class Token {

    private String token;

    private Token() {}

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

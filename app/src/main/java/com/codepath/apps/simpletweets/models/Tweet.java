package com.codepath.apps.simpletweets.models;

/**
 * Created by smacgregor on 2/17/16.
 */
public class Tweet {

    private User user;
    private String text;
    private int retweetCount;
    private int favouritesCount;
    private String createdAt;
    private String idStr;

    public String getText() {
        return text;
    }

    public User getUser() {
        return user;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public long getId() {
        return Long.valueOf(idStr);
    }
}

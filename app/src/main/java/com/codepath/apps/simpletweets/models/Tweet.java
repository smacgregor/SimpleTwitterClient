package com.codepath.apps.simpletweets.models;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by smacgregor on 2/17/16.
 */
@Parcel
public class Tweet {
    User user;
    String text;
    int retweetCount;
    int favouritesCount;
    String createdAt;
    String idStr;
    Entities entities;

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

    public TweetMedia getMedia() {
        return (entities.media != null && entities.media.size() > 0) ? entities.media.get(0) : null;
    }

    @Parcel
    public static class Entities {
        List<TweetMedia> media;
    }
}

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
    int favoriteCount;
    String createdAt;
    String idStr;
    Entities entities;
    ExtendedEntities extendedEntities;

    public String getText() {
        return text;
    }

    public User getUser() {
        return user;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
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

    public TweetVideo getVideo() {
        return (extendedEntities != null && extendedEntities.media != null && extendedEntities.media.size() > 0) ? extendedEntities.media.get(0) : null;
    }

    @Parcel
    public static class Entities {
        List<TweetMedia> media;
    }

    @Parcel
    public static class ExtendedEntities {
        List<TweetVideo> media;
    }
}

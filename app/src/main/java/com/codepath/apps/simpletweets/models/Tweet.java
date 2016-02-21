package com.codepath.apps.simpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by smacgregor on 2/17/16.
 */

@Table(name = "tweets")
public class Tweet extends Model {
    @Column(name="User", onUpdate = Column.ForeignKeyAction.CASCADE)
    public User user;

    @Column String text;
    @Column int retweetCount;
    @Column int favouritesCount;
    @Column String createdAt;

    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    @SerializedName("id") long serverId;

    @Column(name="Entities", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    Entities entities;

    //@Column(name="ExtendedEntities", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    ExtendedEntities extendedEntities;

    public Tweet() {
        super();
    }

    public final Long saveTweet() {
        // onUpdate = Column.ForeignKeyAction.CASCADE is not working for me
        // so for now - hand save our inner classes when we try to save a tweet
        if (user != null) {
            user.save();
        }
        if (entities != null) {
            entities.save();
        }
        return super.save();
    }

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

    public long getServerId() {
        return Long.valueOf(serverId);
    }

    public TweetMedia getMedia() {
        return (entities.media != null && entities.media.size() > 0) ? entities.media.get(0) : null;
    }

    public TweetVideo getVideo() {
        return (extendedEntities != null && extendedEntities.media != null && extendedEntities.media.size() > 0) ? extendedEntities.media.get(0) : null;
    }

    public static class Entities extends Model {
        
        List<TweetMedia> media;

        public Entities() {
            super();
        }
    }

    //@Table(name = "ExtendedEntities")
    public static class ExtendedEntities extends Model {
        List<TweetVideo> media;

        public ExtendedEntities() {
            super();
        }
    }
}

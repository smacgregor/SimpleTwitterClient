package com.codepath.apps.simpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by smacgregor on 2/17/16.
 */

@Table(name = "tweets")
public class Tweet extends Model {
    @Column(name="User")
    public User user;

    @Column String text;
    @Column int retweetCount;
    @Column int favoriteCount;
    @Column String createdAt;

    @Column(name = "remote_id", unique = true, index = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    @SerializedName("id") long serverId;

    @Column(name="Entities", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    Entities entities;

    @Column(name="ExtendedEntities", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    ExtendedEntities extendedEntities;

    public Tweet() {
        super();
    }

    public final Long cascadeSave() {
        // onUpdate = Column.ForeignKeyAction.CASCADE is not working for me
        // so for now - hand save our inner classes when we try to save a tweet
        if (user != null) {
            user.save();
        }

        if (entities != null) {
            entities.cascadeSave();
        }

        if (extendedEntities != null) {
            extendedEntities.cascadeSave();
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

    public long getServerId() {
        return Long.valueOf(serverId);
    }

    public TweetMedia getMedia() {
        List<TweetMedia> media = entities.getMedia();
        return (media != null && media.size() > 0) ? media.get(0) : null;
    }

    public TweetVideo getVideo() {
        List<TweetVideo> videos = null;
        if (extendedEntities != null) {
            extendedEntities.getExtendedMedia();
        }
        return (videos != null &&  videos.size() > 0) ? videos.get(0) : null;
    }

    @Table(name = "Entities")
    public static class Entities extends Model {
        
        List<TweetMedia> media;

        public Entities() {
            super();
        }

        public List<TweetMedia> getMedia() {
            return getMany(TweetMedia.class, "Entities");
        }

        public final Long cascadeSave() {
            long retVal = save();
            if (media != null && media.size() > 0) {
                for (TweetMedia med : media) {
                    med.setEntities(this);
                    med.cascadeSave();
                }
            }
            return retVal;
        }
    }

    @Table(name = "ExtendedEntities")
    public static class ExtendedEntities extends Model {
        List<TweetVideo> media;

        public ExtendedEntities() {
            super();
        }

        public List<TweetVideo> getExtendedMedia() {
            return getMany(TweetVideo.class, "ExtendedEntities");
        }

        public final Long cascadeSave() {
            long retVal = save();
            if (media != null && media.size() > 0) {
                for (TweetVideo med : media) {
                    med.setExtendedEntities(this);
                    med.cascadeSave();
                }
            }
            return retVal;
        }
    }

    public static Tweet findTweet(long serverId) {
        return new Select().from(Tweet.class).where("remote_id = ?", serverId).executeSingle();
    }
}

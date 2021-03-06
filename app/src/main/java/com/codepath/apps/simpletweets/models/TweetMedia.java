package com.codepath.apps.simpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by smacgregor on 2/19/16.
 * Model representing a piece of media inside of a tweet. Currently always a photo
 */

@Table(name = "media")
public class TweetMedia extends Model {

    @Column String type;
    @Column String mediaUrl;
    @Column (name = "Entities") Tweet.Entities mEntities;

    public TweetMedia() {
        super();
    }

    public void setEntities(Tweet.Entities entities) {
        mEntities = entities;
    }

    public Long cascadeSave() {
        return save();
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return mediaUrl;
    }
}

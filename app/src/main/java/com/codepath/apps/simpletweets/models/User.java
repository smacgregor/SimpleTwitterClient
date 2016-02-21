package com.codepath.apps.simpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

/**
 * Created by smacgregor on 2/17/16.
 */

@Table(name = "Users")
public class User extends Model {

    @Column String name;
    @Column String screenName;
    @Column String profileImageUrl;

    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    @SerializedName("id") long serverId;

    public User() {
        super();
    }

    public String getUserName() {
        return name;
    }

    public String getScreenName() {
        return "@" + screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}

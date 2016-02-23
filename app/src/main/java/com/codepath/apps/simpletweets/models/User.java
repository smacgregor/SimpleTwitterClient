package com.codepath.apps.simpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
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

    public long getServerId() {
        return serverId;
    }

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public static User findUser(long serverId) {
        return new Select().from(User.class).where("remote_id = ?", serverId).executeSingle();
    }
}

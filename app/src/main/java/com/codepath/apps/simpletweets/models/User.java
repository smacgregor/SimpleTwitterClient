package com.codepath.apps.simpletweets.models;

import org.parceler.Parcel;

/**
 * Created by smacgregor on 2/17/16.
 */

@Parcel
public class User {

    String name;
    String location;
    String screenName;
    String profileImageUrl;

    public String getUserName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}

package com.codepath.apps.simpletweets.models;

/**
 * Created by smacgregor on 2/17/16.
 */
public class User {

    private String name;
    private String location;
    private String screenName;
    private String profileImageUrl;

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

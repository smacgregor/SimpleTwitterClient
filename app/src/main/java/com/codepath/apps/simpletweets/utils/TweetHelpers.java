package com.codepath.apps.simpletweets.utils;

import android.text.TextUtils;

import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;

/**
 * Created by smacgregor on 2/19/16.
 */
public class TweetHelpers {
    /**
     * Return the best profile image url we can use for user
     * @param user
     * @return
     */
    public static String getBestProfilePictureforUser(User user) {
        String normalProfileUrl = user.getProfileImageUrl();
        // replace _normal with _bigger to get a higher res profile picture
        return normalProfileUrl.replace("_normal", "_bigger");
    }

    public static boolean hasVideo(Tweet tweet) {
        return tweet.getVideo() != null && !TextUtils.isEmpty(tweet.getVideo().getUrl());
    }
}

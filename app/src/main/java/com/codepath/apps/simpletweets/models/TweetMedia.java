package com.codepath.apps.simpletweets.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by smacgregor on 2/19/16.
 * Model representing a piece of media inside of a tweet. Currently always a photo
 */

@Parcel
public class TweetMedia {
    String type;
    String mediaUrl;
    ImageSizes sizes;

    public String getType() {
        return type;
    }

    public String getUrl() {
        return mediaUrl;
    }

    public int getWidth() {
        return sizes.medium.width;
    }

    public int getHeight() {
        return sizes.medium.height;
    }

    @Parcel
    public static class ImageSizes {
        ImageSize medium;

        @Parcel
        public static class ImageSize {
            @SerializedName("w")
            int width;

            @SerializedName("h")
            int height;
        }
    }
}

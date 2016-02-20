package com.codepath.apps.simpletweets.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by smacgregor on 2/20/16.
 */

@Parcel
public class TweetVideo extends TweetMedia {

    VideoInfo videoInfo;

    @Override
    public String getUrl() {
        // TODO - add a helper that returns the first mp4 flavor?
        return (videoInfo != null && videoInfo.flavors.size() > 0) ? videoInfo.flavors.get(0).url : null;
    }

    @Parcel
    public static class VideoInfo {

        @SerializedName("variants")
        List<Flavors> flavors;

        @Parcel
        public static class Flavors {
            String contentType;
            String url;
        }
    }
}

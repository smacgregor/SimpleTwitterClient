package com.codepath.apps.simpletweets.models;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by smacgregor on 2/20/16.
 */

public class TweetVideo extends TweetMedia {

    @Column(name="VideoInfo", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    VideoInfo videoInfo;

    @Override
    public String getUrl() {
        // TODO - add a helper that returns the first mp4 flavor?
        return (videoInfo != null && videoInfo.flavors.size() > 0) ? videoInfo.flavors.get(0).url : null;
    }

    @Table(name="VideoInfo")
    public static class VideoInfo {

        @SerializedName("variants")
        List<Flavor> flavors;

        public VideoInfo() {
            super();
        }

        @Table(name = "Flavor")
        public static class Flavor {
            @Column String contentType;
            @Column String url;

            public Flavor() {
                super();
            }
        }
    }
}

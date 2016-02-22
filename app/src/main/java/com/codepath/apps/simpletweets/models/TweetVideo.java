package com.codepath.apps.simpletweets.models;

import com.activeandroid.Model;
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

    @Column (name = "ExtendedEntities") Tweet.ExtendedEntities mExtendedEntities;

    public TweetVideo() {
        super();
    }

    public void setExtendedEntities(Tweet.ExtendedEntities entities) {
        mExtendedEntities = entities;
    }

    @Override
    public Long cascadeSave() {
        if (videoInfo != null) {
            videoInfo.cascadeSave();
        }
        return save();
    }

    @Override
    public String getUrl() {
        // TODO - add a helper that returns the first mp4 flavor?
        List<TweetVideo.VideoInfo.Flavor> flavors = null;
        if (videoInfo != null) {
            flavors = videoInfo.getFlavors();
        }
        return (flavors != null && flavors.size() > 0) ? flavors.get(0).url : null;
    }

    @Table(name="VideoInfo")
    public static class VideoInfo extends Model {

        @SerializedName("variants") List<Flavor> flavors;

        public VideoInfo() {
            super();
        }

        public Long cascadeSave() {
            long retVal = save();
            if (flavors != null && flavors.size() > 0) {
                for (Flavor flavor : flavors) {
                    flavor.setVideoInfo(this);
                    flavor.cascadeSave();
                }
            }
            return retVal;
        }

        public List<Flavor> getFlavors() {
            return getMany(Flavor.class, "VideoInfo");
        }

        @Table(name = "Flavor")
        public static class Flavor extends Model {
            @Column String contentType;
            @Column String url;
            @Column (name = "VideoInfo") TweetVideo.VideoInfo mVideoInfo;

            public Flavor() {
                super();
            }

            public final Long cascadeSave() {
                return save();
            }

            public void setVideoInfo(VideoInfo videoInfo) {
                mVideoInfo = videoInfo;
            }
        }
    }
}

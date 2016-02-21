package com.codepath.apps.simpletweets;

import android.util.Log;

import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by smacgregor on 2/18/16.
 */
public class TwitterManager {

    private static TwitterManager mInstance = new TwitterManager();
    private TwitterClient mTwitterClient;

    private TwitterManager() {
        mTwitterClient = TwitterApplication.getRestClient();
    }

    public static TwitterManager getInstance() {
        return mInstance;
    }

    public void fetchCurrentUser(final OnCurrentUserReceivedListener listener) {
        mTwitterClient.getCurrentUser(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onUserFailed(statusCode, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                listener.onUserReceived(parseUserFromJSON(responseString));
            }
        });
    }

    public void fetchTimelineTweets(long oldestTweetId, long lastSeenTweetId, final OnTimelineTweetsReceivedListener listener) {
        mTwitterClient.getHomeTimeline(25, oldestTweetId, lastSeenTweetId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "failed to get a response from twitter", throwable);
                listener.onTweetsFailed(statusCode, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                listener.onTweetsReceived(parseTweetsFromJSON(responseString));
            }
        });
    }

    public void postUpdate(final String postContents, long inReplyToStatusId, final OnNewPostReceivedListener listener) {
        mTwitterClient.postUpdate(postContents, inReplyToStatusId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onPostFailed(statusCode, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                listener.onPostCreated(parseTweetFromJSON(responseString));
            }
        });
    }

    public void markAsFavorite(long tweetId, final OnTweetUpdatedListener listener) {
        mTwitterClient.markAsFavorite(tweetId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "failed to update favorite status", throwable);
                listener.onTweetUpdateFailed(statusCode, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                listener.onTweetUpdated(parseTweetFromJSON(responseString));
            }
        });
    }

    public void retweet(long tweetId, final OnTweetUpdatedListener listener) {
        mTwitterClient.retweet(tweetId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onTweetUpdateFailed(statusCode, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                listener.onTweetUpdated(parseTweetFromJSON(responseString));
            }
        });
    }

    private Tweet parseTweetFromJSON(String response) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        return gson.fromJson(response, Tweet.class);
    }

    private List<Tweet> parseTweetsFromJSON(String response) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        Type collectionType = new TypeToken<List<Tweet>>(){}.getType();
        return gson.fromJson(response, collectionType);
    }

    private User parseUserFromJSON(String jsonResponse) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        Type collectionType = new TypeToken<User>(){}.getType();
        return gson.fromJson(jsonResponse, collectionType);
    }


    public interface OnTimelineTweetsReceivedListener {
        void onTweetsReceived(List<Tweet> tweets);
        void onTweetsFailed(int statusCode, Throwable throwable);
    }

    public interface OnCurrentUserReceivedListener {
        void onUserReceived(User user);
        void onUserFailed(int statusCode, Throwable throwable);
    }

    public interface OnNewPostReceivedListener {
        void onPostCreated(Tweet tweet);
        void onPostFailed(int statusCode, Throwable throwable);
    }

    public interface OnTweetUpdatedListener {
        void onTweetUpdated(Tweet tweet);
        void onTweetUpdateFailed(int statusCode, Throwable throwable);
    }
}

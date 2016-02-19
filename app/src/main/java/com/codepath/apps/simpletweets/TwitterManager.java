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

    /**
     * Fetch the next batch of tweets older than mOldestTweetId.
     */
    public void fetchTweetsForTimeline(long oldestTweetId, final OnTimelineTweetsReceivedListener listener) {
        mTwitterClient.getHomeTimeline(25, oldestTweetId, new TextHttpResponseHandler() {
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
}

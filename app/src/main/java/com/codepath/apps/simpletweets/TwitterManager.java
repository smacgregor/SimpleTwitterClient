package com.codepath.apps.simpletweets;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by smacgregor on 2/18/16.
 */
public class TwitterManager {

    private static int PAGE_SIZE = 25;

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
                User currentUser = parseUserFromJSON(responseString);
                if (currentUser != null) {
                    User originalUser = User.findUser(currentUser.getServerId());
                    if (originalUser != null) {
                        currentUser = originalUser;
                    } else {
                        currentUser.save();
                    }
                }
                listener.onUserReceived(currentUser);
            }
        });
    }

    public void fetchTimelineTweets(long oldestTweetId, long lastSeenTweetId, final OnTimelineTweetsReceivedListener listener) {
        if (mTwitterClient.isOnline()) {
            mTwitterClient.getHomeTimeline(PAGE_SIZE, oldestTweetId, lastSeenTweetId, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    listener.onTweetsFailed(statusCode, throwable);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    List<Tweet> tweets = parseTweetsFromJSON(responseString);
                    saveTweets(tweets);
                    listener.onTweetsReceived(tweets);
                }
            });
        } else {
            List<Tweet> queryResults;
            if (oldestTweetId > 0) {
                queryResults = new Select().
                        from(Tweet.class).
                        where("remote_id < ?", oldestTweetId).
                        orderBy("remote_id DESC").
                        limit(PAGE_SIZE).execute();
            } else {
                queryResults = new Select().
                        from(Tweet.class).
                        orderBy("remote_id DESC").
                        limit(PAGE_SIZE).execute();
            }
            listener.onTweetsReceived(queryResults);
        }
    }

    public void fetchMentionTweets(long oldestTweetId, long lastSeenTweetId, final OnTimelineTweetsReceivedListener listener) {
        if (mTwitterClient.isOnline()) {
            mTwitterClient.getMentionsTimeline(PAGE_SIZE, oldestTweetId, lastSeenTweetId, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    listener.onTweetsFailed(statusCode, throwable);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    List<Tweet> tweets = parseTweetsFromJSON(responseString);
                    saveTweets(tweets);
                    listener.onTweetsReceived(tweets);
                }
            });
        } else {
            listener.onTweetsFailed(0, null);
        }
    }

    public void postUpdate(final String postContents, long inReplyToStatusId, final OnNewPostReceivedListener listener) {
        mTwitterClient.postUpdate(postContents, inReplyToStatusId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onPostFailed(statusCode, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Tweet tweet = parseTweetFromJSON(responseString);
                // TODO - move to a background thread
                tweet.save();
                listener.onPostCreated(tweet);
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
                Tweet tweet = parseTweetFromJSON(responseString);
                Tweet original = Tweet.findTweet(tweet.getServerId());
                if (original != null) {
                    tweet = original;
                } else {
                    tweet.save();
                }
                listener.onTweetUpdated(tweet);
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
                Tweet tweet = parseTweetFromJSON(responseString);
                Tweet original = Tweet.findTweet(tweet.getServerId());
                if (original != null) {
                    tweet = original;
                } else {
                    tweet.save();
                }
                listener.onTweetUpdated(tweet);
            }
        });
    }

    private Tweet parseTweetFromJSON(String response) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        return gson.fromJson(response, Tweet.class);
    }

    private List<Tweet> parseTweetsFromJSON(String response) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).
                excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC).
                create();
        Type collectionType = new TypeToken<List<Tweet>>(){}.getType();
        return gson.fromJson(response, collectionType);
    }

    private User parseUserFromJSON(String jsonResponse) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).
                excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC).
                create();
        Type collectionType = new TypeToken<User>(){}.getType();
        return gson.fromJson(jsonResponse, collectionType);
    }

    private void saveTweets(List<Tweet> tweets) {
        // TODO - This save should be done on a background thread
        ActiveAndroid.beginTransaction();
        try {
            for (Tweet tweet : tweets) {
                // does the tweet exist already?
                Tweet original = Tweet.findTweet(tweet.getServerId());
                if (original != null) {
                    tweets.set(tweets.indexOf(tweet), original);
                } else {
                    tweet.cascadeSave();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
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

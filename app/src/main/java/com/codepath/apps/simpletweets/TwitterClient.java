package com.codepath.apps.simpletweets;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.io.IOException;

/*
 * 
 * This is the object responsible for communicating with the Twitter API.
 */
public class TwitterClient extends OAuthBaseClient {

	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "pEJuD8GpDWnm5t73hz0BaciGk";
	public static final String REST_CONSUMER_SECRET = "bbjttMOcPms6Qwn9IFKXbgBErnlVngYBO20A4GLWCHQDGN7lEN";
	public static final String REST_CALLBACK_URL = "oauth://smsimpletweets";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public boolean isOnline() {
		return isNetworkOnline() && isNetworkAvailable(context);
	}

	/**
	 * Return a set of tweets for the current users timeline
	 * @param pageSize
	 * @param oldestTweetId - return results older than this id
	 * @param lastSeenTweetId - return results more recent than this id
	 * @param handler
	 */
	public void getHomeTimeline(int pageSize, long oldestTweetId, long lastSeenTweetId, TextHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
        getTimeline("statuses/home_timeline.json", 0, pageSize, oldestTweetId, lastSeenTweetId, handler);
	}


	public void getMentionsTimeline(int pageSize, long oldestTweetId, long lastSeenTweetId, TextHttpResponseHandler handler) {
        getTimeline("statuses/mentions_timeline.json", 0, pageSize, oldestTweetId, lastSeenTweetId, handler);
}

	public void getUsersTimeline(long userId, int pageSize, long oldestTweetId, long lastSeenTweetId, TextHttpResponseHandler handler) {
        getTimeline("statuses/user_timeline.json", userId, pageSize, oldestTweetId, lastSeenTweetId, handler);
	}

	/**
	 * Fetch the current user
	 * @param handler
	 */
	public void getCurrentUser(TextHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl, null, handler);
	}

	/**
	 * Post an update
	 * @param statusText
	 * @param inReplyToStatusId
	 * @param handler
	 */
	public void postUpdate(String statusText, long inReplyToStatusId, TextHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", statusText);
		if (inReplyToStatusId > 0) {
			params.put("in_reply_to_status_id", inReplyToStatusId);
		}
		client.post(apiUrl, params, handler);
	}

	/**
	 * Mark the tweet as a favorite
	 * @param tweetId
	 * @param handler
	 */
	public void markAsFavorite(long tweetId,  TextHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		client.post(apiUrl, params, handler);
	}

	/**
	 * Mark the tweet as a favorite
	 * @param tweetId
	 * @param handler
	 */
	public void retweet(long tweetId,  TextHttpResponseHandler handler) {
		String apiUrl = String.format("%s%d.json", getApiUrl("statuses/retweet/"), tweetId);
		client.post(apiUrl, null, handler);
	}

	/**
	 * Report on the availablity of a network connection
	 * @return
	 */
	private boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	private boolean isNetworkOnline() {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
			int     exitValue = ipProcess.waitFor();
			return (exitValue == 0);
		} catch (IOException e)          { e.printStackTrace(); }
		catch (InterruptedException e) { e.printStackTrace(); }
		return false;
	}

    private void getTimeline(String endPoint, long userId, int pageSize, long tweetMaxId, long lastSeenTweetId, TextHttpResponseHandler handler) {
        String apiUrl = getApiUrl(endPoint);
        RequestParams params = new RequestParams();
        params.put("count", pageSize);

        if (tweetMaxId > 0) {
            params.put("max_id", tweetMaxId - 1);
        }

        if (lastSeenTweetId > 0) {
            params.put("since_id", lastSeenTweetId);
        }

        if (userId > 0) {
            params.put("user_id", userId);
        }

        client.get(apiUrl, params, handler);

    }
}
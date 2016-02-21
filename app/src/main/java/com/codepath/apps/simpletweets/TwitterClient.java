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
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
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

	/**
	 * Return a set of tweets for the current users timeline
	 * @param count
	 * @param tweetMaxId - return results older than this id
	 * @param lastSeenTweetId - return results more recent than this id
	 * @param handler
	 */
	public void getHomeTimeline(int count, long tweetMaxId, long lastSeenTweetId, TextHttpResponseHandler handler) {
		if (isOnline() && isNetworkAvailable(context)) {
			String apiUrl = getApiUrl("statuses/home_timeline.json");
			RequestParams params = new RequestParams();
			params.put("count", count);

			if (tweetMaxId > 0) {
				params.put("max_id", tweetMaxId - 1);
			}

			if (lastSeenTweetId > 0) {
				params.put("since_id", lastSeenTweetId);
			}

			client.get(apiUrl, params, handler);
		} else {
			handler.onFailure(0, null, "", null);
		}
	}

	/**
	 * Fetch the current user
	 * @param handler
	 */
	public void getCurrentUser(TextHttpResponseHandler handler) {
		if (isOnline() && isNetworkAvailable(context)) {
			String apiUrl = getApiUrl("account/verify_credentials.json");
			client.get(apiUrl, null, handler);
		} else {
			handler.onFailure(0, null, "", null);
		}
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

	private boolean isOnline() {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
			int     exitValue = ipProcess.waitFor();
			return (exitValue == 0);
		} catch (IOException e)          { e.printStackTrace(); }
		catch (InterruptedException e) { e.printStackTrace(); }
		return false;
	}
}
package com.codepath.apps.simpletweets;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

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
	 * @param tweetMaxId
	 * @param handler
	 */
	public void getHomeTimeline(int count, long tweetMaxId, TextHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", count);

		if (tweetMaxId > 0) {
			params.put("max_id", tweetMaxId - 1);
		}

		client.get(apiUrl, params, handler);
	}

	/**
	 * Fetch the current user
	 * @param handler
	 */
	public void getCurrentUser(TextHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl, null, handler);
	}
}
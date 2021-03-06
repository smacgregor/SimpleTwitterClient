package com.codepath.apps.simpletweets.fragments;

import android.support.v4.widget.SwipeRefreshLayout;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterManager;
import com.codepath.apps.simpletweets.models.Tweet;

import java.util.List;

/**
 * Created by smacgregor on 2/23/16.
 */
public class HomeTimelineFragment extends TweetsTimelineFragment {

    /**
     * Fetch the next batch of tweets older than mOldestTweetId.
     */
    @Override
    public void fetchTweetsForTimeline() {
        showProgressbar();
        TwitterManager.getInstance().fetchTimelineTweets(getOldestTweetId(), 0, new TwitterManager.OnTimelineTweetsReceivedListener() {
            @Override
            public void onTweetsReceived(List<Tweet> tweets) {
                hideProgressbar();
                appendTweets(tweets);
            }

            @Override
            public void onTweetsFailed(int statusCode, Throwable throwable) {
                displayAlertMessage(getResources().getString(R.string.error_no_network));
            }

        });
    }

    @Override
    public void setupSwipeToRefresh() {
        super.setupSwipeToRefresh();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TwitterManager.getInstance().fetchTimelineTweets(0, getNewestTweetId(), new TwitterManager.OnTimelineTweetsReceivedListener() {
                    @Override
                    public void onTweetsReceived(List<Tweet> tweets) {
                        prependTweets(tweets, false);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onTweetsFailed(int statusCode, Throwable throwable) {
                        displayAlertMessage(getResources().getString(R.string.error_no_network));
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

    }
}
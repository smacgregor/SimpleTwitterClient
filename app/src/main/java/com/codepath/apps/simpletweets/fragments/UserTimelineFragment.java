package com.codepath.apps.simpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterManager;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;

import java.util.List;

/**
 * Created by smacgregor on 2/24/16.
 */
public class UserTimelineFragment extends TweetsTimelineFragment {

    private static final String ARGUMENT_USER_ID = "ARGUMENT_USER_ID";

    public static UserTimelineFragment newInstance(long userId) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putLong(ARGUMENT_USER_ID, userId);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long userId = getArguments().getLong(ARGUMENT_USER_ID);
        setUser(User.findUser(userId));
    }

    /**
     * Fetch the next batch of tweets older than mOldestTweetId.
     */
    @Override
    public void fetchTweetsForTimeline() {
        showProgressbar();
        TwitterManager.getInstance().fetchUserTimeline(getUser().getServerId(), getOldestTweetId(), 0, new TwitterManager.OnTimelineTweetsReceivedListener() {
            @Override
            public void onTweetsReceived(List<Tweet> tweets) {
                hideProgressbar();
                appendTweets(tweets);
            }

            @Override
            public void onTweetsFailed(int statusCode, Throwable throwable) {
                hideProgressbar();
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
                TwitterManager.getInstance().fetchUserTimeline(getUser().getServerId(), 0, getNewestTweetId(), new TwitterManager.OnTimelineTweetsReceivedListener() {
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

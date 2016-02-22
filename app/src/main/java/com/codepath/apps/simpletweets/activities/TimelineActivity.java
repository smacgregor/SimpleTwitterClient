package com.codepath.apps.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterManager;
import com.codepath.apps.simpletweets.adapters.TweetsAdapter;
import com.codepath.apps.simpletweets.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class TimelineActivity extends AppCompatActivity
        implements FloatingActionButton.OnClickListener,
        TweetsAdapter.OnItemClickListener,
        ComposeTweetDialogFragment.OnComposeDialogFragmentListener {

    @Bind(R.id.recycler_timeline) RecyclerView mTweetsView;
    @Bind(R.id.fab_compose_tweet) FloatingActionButton mFloatingActionButton;
    @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Tweet> mTweets;
    private TweetsAdapter mTweetsAdapter;
    private long mOldestTweetId;
    private long mNewestTweetId;
    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO - make this an actual singleton instead of hanging a global
        // off of the application. Code smell.
        mTweets = new ArrayList<>();
        mOldestTweetId = 0;
        mNewestTweetId = 0;

        mFloatingActionButton.setOnClickListener(this);

        setupSwipeToRefresh();
        setupTweetListView();
        fetchTweetsForTimeline();
        fetchCurrentUser();
    }

    @Override
    /**
     * Floating action bary on click handler
     */
    public void onClick(View v) {
        composeNewTweet(null);
    }

    @Override
    public void onPostedTweet(Tweet newTweetPost) {
        if (newTweetPost != null) {
            mTweets.add(0, newTweetPost);
            mTweetsAdapter.notifyItemRangeInserted(0, 1);
            mNewestTweetId = newTweetPost.getServerId();
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mTweetsView.getLayoutManager();
            linearLayoutManager.scrollToPositionWithOffset(0, 20);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Tweet tweet = mTweets.get(position);
        switch (view.getId()) {
            case R.id.button_retweet:
                retweet(tweet);
                break;
            case R.id.button_reply:
                composeNewTweet(tweet);
                break;
            case R.id.button_favorite:
                markTweetAsFavorite(tweet);
                break;
            default:
                openTweet(mTweets.get(position));
                break;
        }
    }

    /**
     * Open a compose tweet dialog fragment to allow the user to compose a new tweet
     * @param replyToTweet
     */
    private void composeNewTweet(Tweet replyToTweet) {
        ComposeTweetDialogFragment tweetDialogFragment = ComposeTweetDialogFragment.newInstance(mCurrentUser, replyToTweet);
        tweetDialogFragment.show(getSupportFragmentManager(), "fragment_compose_tweet_dialog");
    }

    /**
     * Open the details activity for a tweet
     * @param tweet
     */
    private void openTweet(Tweet tweet) {
        Intent intent = TweetDetailsActivity.getStartIntent(this, tweet, mCurrentUser);
        startActivity(intent);
    }

    private void setupTweetListView() {
        mTweetsAdapter = new TweetsAdapter(mTweets);
        mTweetsView.setAdapter(new AlphaInAnimationAdapter(mTweetsAdapter));
        mTweetsAdapter.setOnItemClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mTweetsView.setLayoutManager(layoutManager);
        mTweetsView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchTweetsForTimeline();
            }
        });
    }

    private void setupSwipeToRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TwitterManager.getInstance().fetchTimelineTweets(0, mNewestTweetId, new TwitterManager.OnTimelineTweetsReceivedListener() {
                    @Override
                    public void onTweetsReceived(List<Tweet> tweets) {
                        if (tweets.size() > 0) {
                            mTweets.addAll(0, tweets);
                            mTweetsAdapter.notifyItemRangeInserted(0, tweets.size());
                            mNewestTweetId = tweets.get(0).getServerId();
                        }
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
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright);
    }

    /**
     * Fetch the next batch of tweets older than mOldestTweetId.
     */
    private void fetchTweetsForTimeline() {
        TwitterManager.getInstance().fetchTimelineTweets(mOldestTweetId, 0, new TwitterManager.OnTimelineTweetsReceivedListener() {
            @Override
            public void onTweetsReceived(List<Tweet> tweets) {
                if (tweets.size() > 0) {
                    mTweets.addAll(tweets);
                    mTweetsAdapter.notifyItemRangeInserted(mTweetsAdapter.getItemCount(), tweets.size());
                    mOldestTweetId = tweets.get(tweets.size() - 1).getServerId();
                    if (mNewestTweetId == 0) {
                        mNewestTweetId = tweets.get(0).getServerId();
                    }
                }
            }

            @Override
            public void onTweetsFailed(int statusCode, Throwable throwable) {
                displayAlertMessage(getResources().getString(R.string.error_no_network));
            }
        });
    }

    private void fetchCurrentUser() {
        TwitterManager.getInstance().fetchCurrentUser(new TwitterManager.OnCurrentUserReceivedListener() {
            @Override
            public void onUserReceived(User user) {
                mCurrentUser = user;
            }

            @Override
            public void onUserFailed(int statusCode, Throwable throwable) {}
        });
    }

    private void markTweetAsFavorite(final Tweet tweet) {
        TwitterManager.getInstance().markAsFavorite(tweet.getServerId(), new TwitterManager.OnTweetUpdatedListener() {
            @Override
            public void onTweetUpdated(Tweet updatedTweet) {
                // this will be easier when tweets are in a local db
                tweet.setFavoriteCount(updatedTweet.getFavoriteCount());
                mTweetsAdapter.notifyItemChanged(mTweets.indexOf(tweet));
            }

            @Override
            public void onTweetUpdateFailed(int statusCode, Throwable throwable) {
                displayAlertMessage(getResources().getString(R.string.error_favorite_failed));
            }
        });
    }

    private void retweet(final Tweet tweet) {
        TwitterManager.getInstance().retweet(tweet.getServerId(), new TwitterManager.OnTweetUpdatedListener() {
            @Override
            public void onTweetUpdated(Tweet updatedTweet) {
                // this will be easier when tweets are in a local db
                tweet.setRetweetCount(updatedTweet.getRetweetCount());
                mTweetsAdapter.notifyItemChanged(mTweets.indexOf(tweet));
            }

            @Override
            public void onTweetUpdateFailed(int statusCode, Throwable throwable) {
                displayAlertMessage(getResources().getString(R.string.error_retweet_failed));
            }
        });
    }

    /**
     * Alert the user that their internet connection may be down /
     * the server returned an error
     */
    private void displayAlertMessage(final String alertMessage) {
        Snackbar.make(findViewById(android.R.id.content), alertMessage,
                Snackbar.LENGTH_LONG).show();
    }
}

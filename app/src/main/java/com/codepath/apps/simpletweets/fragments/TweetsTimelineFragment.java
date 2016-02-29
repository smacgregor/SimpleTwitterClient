package com.codepath.apps.simpletweets.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterManager;
import com.codepath.apps.simpletweets.activities.ProfileActivity;
import com.codepath.apps.simpletweets.activities.TweetDetailsActivity;
import com.codepath.apps.simpletweets.adapters.TweetsAdapter;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Created by smacgregor on 2/23/16.
 */
public class TweetsTimelineFragment extends Fragment implements TweetsAdapter.OnItemClickListener {

    @Bind(R.id.recycler_timeline) RecyclerView mTweetsView;
    @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Tweet> mTweets;
    private TweetsAdapter mTweetsAdapter;

    private long mOldestTweetId;
    private long mNewestTweetId;
    private User mUser;
    private OnTweetsDialogFragmentListener mOnTweetsDialogFragmentListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        ButterKnife.bind(this, view);

        setupSwipeToRefresh();
        setupTweetListView();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTweets = new ArrayList<>();
        mOldestTweetId = 0;
        mNewestTweetId = 0;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchTweetsForTimeline();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTweetsDialogFragmentListener) {
            mOnTweetsDialogFragmentListener = (OnTweetsDialogFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTweetsDialogFragmentListener");
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
                replyToTweet(tweet);
                break;
            case R.id.button_favorite:
                markTweetAsFavorite(tweet);
                break;
            case R.id.image_profile:
                showProfile(tweet.getUser());
                break;
            default:
                openTweet(mTweets.get(position));
                break;
        }
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public long getOldestTweetId() {
        return mOldestTweetId;
    }

    public long getNewestTweetId() {
        return mNewestTweetId;
    }

    private void setupTweetListView() {
        mTweetsAdapter = new TweetsAdapter(mTweets);
        mTweetsView.setAdapter(new AlphaInAnimationAdapter(mTweetsAdapter));
        mTweetsAdapter.setOnItemClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mTweetsView.setLayoutManager(layoutManager);
        mTweetsView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchTweetsForTimeline();
            }
        });
    }

    /**
     * Override this
     */
    public void fetchTweetsForTimeline() {}

    public void showProgressbar() {
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(null);
        appendTweets(tweets);
    }

    public void hideProgressbar() {
        removeTweet(null);
    }

    public void appendTweets(List<Tweet> tweets) {
        mTweets.addAll(tweets);
        mTweetsAdapter.notifyItemRangeInserted(mTweetsAdapter.getItemCount(), tweets.size());
        Tweet oldestTweet = tweets.get(tweets.size() - 1);
        if (oldestTweet != null) {
            mOldestTweetId = oldestTweet.getServerId();
        }
        Tweet newestTweet = tweets.get(0);
        if (newestTweet != null && mNewestTweetId == 0) {
            mNewestTweetId = newestTweet.getServerId();
        }
    }

    public void prependTweets(List<Tweet> tweets, boolean scrollToFirstItem) {
        if (tweets.size() > 0) {
            mTweets.addAll(0, tweets);
            mTweetsAdapter.notifyItemRangeInserted(0, tweets.size());
            mNewestTweetId = tweets.get(0).getServerId();

            if (scrollToFirstItem) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mTweetsView.getLayoutManager();
                linearLayoutManager.scrollToPositionWithOffset(0, 20);
            }
        }
    }

    public void setupSwipeToRefresh() {
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright);
    }

    /**
     * Alert the user that their internet connection may be down /
     * the server returned an error
     */
    public void displayAlertMessage(final String alertMessage) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), alertMessage,
                Snackbar.LENGTH_LONG).show();
    }

    private void removeTweet(Tweet tweet) {
        mTweets.remove(tweet);
        mTweetsAdapter.notifyItemRemoved(mTweetsAdapter.getItemCount() - 1);
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
     * Open the details activity for a tweet
     * @param tweet
     */
    private void openTweet(Tweet tweet) {
        Intent intent = TweetDetailsActivity.getStartIntent(getActivity(), tweet, TwitterManager.getInstance().getCurrentUser());
        startActivity(intent);
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

    private void replyToTweet(final Tweet tweet) {
        if (mOnTweetsDialogFragmentListener != null) {
            mOnTweetsDialogFragmentListener.onReplyToTweet(tweet);
        }
    }

    private void showProfile(User user) {
        Intent intent = ProfileActivity.getStartIntent(getActivity(), user);
        startActivity(intent);
    }

    public interface OnTweetsDialogFragmentListener {
        void onReplyToTweet(Tweet newTweetPost);
    }
}

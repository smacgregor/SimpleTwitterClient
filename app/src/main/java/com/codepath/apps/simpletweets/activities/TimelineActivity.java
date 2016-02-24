package com.codepath.apps.simpletweets.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.simpletweets.fragments.TweetsListFragment;
import com.codepath.apps.simpletweets.models.Tweet;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity
        implements FloatingActionButton.OnClickListener,
        ComposeTweetDialogFragment.OnComposeDialogFragmentListener {

    @Bind(R.id.fab_compose_tweet) FloatingActionButton mFloatingActionButton;
    TweetsListFragment mTweetsListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            mTweetsListFragment = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
        }
        mFloatingActionButton.setOnClickListener(this);
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
            List<Tweet> newTweets = new ArrayList<>();
            newTweets.add(newTweetPost);
            mTweetsListFragment.prependTweets(newTweets, true);
        }
    }

    /**
     * Open a compose tweet dialog fragment to allow the user to compose a new tweet
     * @param replyToTweet
     */
    private void composeNewTweet(Tweet replyToTweet) {
        ComposeTweetDialogFragment tweetDialogFragment = ComposeTweetDialogFragment.newInstance(mTweetsListFragment.getCurrentUser(),
                replyToTweet);
        tweetDialogFragment.show(getSupportFragmentManager(), "fragment_compose_tweet_dialog");
    }
}

package com.codepath.apps.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterManager;
import com.codepath.apps.simpletweets.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.simpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.simpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.simpletweets.fragments.TweetsTimelineFragment;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.SmartFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity
        implements FloatingActionButton.OnClickListener,
        TweetsTimelineFragment.OnTweetsDialogFragmentListener,
        ComposeTweetDialogFragment.OnComposeDialogFragmentListener {

    @Bind(R.id.viewpager) ViewPager mViewPager;
    @Bind(R.id.sliding_tabs) TabLayout mTabLayout;
    @Bind(R.id.fab_compose_tweet) FloatingActionButton mFloatingActionButton;

    TweetsPagerAdapter mTweetsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFloatingActionButton.setOnClickListener(this);
        mTweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTweetsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_profile) {
            showProfile(TwitterManager.getInstance().getCurrentUser());
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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

            // add it to the current timeline
            TweetsTimelineFragment tweetsTimelineFragment = (TweetsTimelineFragment) mTweetsPagerAdapter.getRegisteredFragment(0);
            tweetsTimelineFragment.prependTweets(newTweets, true);
        }
    }

    @Override
    public void onReplyToTweet(Tweet newTweetPost) {
        composeNewTweet(newTweetPost);
    }

    /**
     * Launch the profile activity for the user
     * @param user
     */
    public void showProfile(User user) {
        Intent intent = ProfileActivity.getStartIntent(this, user);
        startActivity(intent);
    }

    /**
     * Open a compose tweet dialog fragment to allow the user to compose a new tweet
     * @param replyToTweet
     */
    private void composeNewTweet(Tweet replyToTweet) {
        ComposeTweetDialogFragment tweetDialogFragment = ComposeTweetDialogFragment.newInstance(TwitterManager.getInstance().getCurrentUser(),
                replyToTweet);
        tweetDialogFragment.show(getSupportFragmentManager(), "fragment_compose_tweet_dialog");
    }

    // Return the order of the fragments in the view pager
    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
        // TODO - localize these strings
        private String tabTitles[] = {"Timeline", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HomeTimelineFragment();
                default:
                    return new MentionsTimelineFragment();
            }
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}

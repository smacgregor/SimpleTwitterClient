package com.codepath.apps.simpletweets.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterManager;
import com.codepath.apps.simpletweets.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.simpletweets.fragments.TweetsTimelineFragment;
import com.codepath.apps.simpletweets.fragments.UserProfileFragment;
import com.codepath.apps.simpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;

import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity implements
        ComposeTweetDialogFragment.OnComposeDialogFragmentListener,
        TweetsTimelineFragment.OnTweetsDialogFragmentListener {

    private static String EXTRA_USER_ID = "com.codepath.apps.simpletweets.activities.profile.userid";

    private long mCurrentUserId;

    public static Intent getStartIntent(Context context, User currentUser) {
        Intent intent= new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_ID, currentUser.getServerId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mCurrentUserId = getIntent().getLongExtra(EXTRA_USER_ID, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            setupUserTimeline();
            setupUserProfile();
        }

        User user = User.findUser(mCurrentUserId);
        setupActionBarTitle(user);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onReplyToTweet(Tweet newTweetPost) {
        ComposeTweetDialogFragment tweetDialogFragment = ComposeTweetDialogFragment.newInstance(TwitterManager.getInstance().getCurrentUser(),
                newTweetPost);
        tweetDialogFragment.show(getSupportFragmentManager(), "fragment_compose_tweet_dialog");
    }

    @Override
    public void onPostedTweet(Tweet newTweetPost) {
        // do nothing
    }

    private void setupUserTimeline() {
        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(mCurrentUserId);
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.layout_timeline_placeholder, userTimelineFragment).
                commit();
    }

    private void setupUserProfile() {
        UserProfileFragment userProfileFragment = UserProfileFragment.newInstance(mCurrentUserId);
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.layout_user_profile_placeholder, userProfileFragment).
                commit();
    }
    private void setupActionBarTitle(User user) {
        getSupportActionBar().setTitle(user.getScreenName());
    }
}

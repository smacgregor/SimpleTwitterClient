package com.codepath.apps.simpletweets.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.TweetHelpers;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    private static String EXTRA_USER_ID = "com.codepath.apps.simpletweets.activities.profile.userid";

    @Bind(R.id.layout_timeline_placholder) FrameLayout mUserTimelinePlaceHolder;
    @Bind(R.id.image_profile) RoundedImageView mProfileImage;
    @Bind(R.id.text_name) TextView mUserName;
    @Bind(R.id.text_screen_name) TextView mScreenName;
    @Bind(R.id.image_profile_background) ImageView mProfileBackground;
    @Bind(R.id.layout_profile_header) RelativeLayout mRelativeLayout;
    @Bind(R.id.text_followers) TextView mFollowersTextView;
    @Bind(R.id.text_following) TextView mFollowingTextView;
    @Bind(R.id.text_user_description) TextView mUserDescription;

    private long mCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mCurrentUserId = getIntent().getLongExtra(EXTRA_USER_ID, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (savedInstanceState == null) {
            setupUserTimeline();
        }
        User user = User.findUser(mCurrentUserId);
        setupActionBarTitle(user);
        setupUser(user);
    }

    public static Intent getStartIntent(Context context, User currentUser) {
        Intent intent= new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_ID, currentUser.getServerId());
        return intent;
    }

    private void setupUserTimeline() {
        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(mCurrentUserId);
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.layout_timeline_placholder, userTimelineFragment).
                commit();
    }

    private void setupActionBarTitle(User user) {
        getSupportActionBar().setTitle(user.getScreenName());
    }

    private void setupUser(User user) {
        Glide.with(mProfileImage.getContext()).
                load(TweetHelpers.getBestProfilePictureforUser(user)).
                into(mProfileImage);

        Glide.with(mProfileBackground.getContext()).
                load(user.getProfileBackgroundImageUrl()).
                fitCenter().
                into(mProfileBackground);

        mUserName.setText(user.getUserName());
        mScreenName.setText(user.getScreenName());

        mFollowersTextView.setText(this.getResources().getQuantityString(R.plurals.numberOfFollowers,
                user.getFollowersCount(), user.getFollowersCount()));

        mFollowingTextView.setText(this.getResources().getString(R.string.numberOfFollowing, user.getFollowingCount()));

        mUserDescription.setText(user.getDescription());
    }

}

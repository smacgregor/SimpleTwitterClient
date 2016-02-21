package com.codepath.apps.simpletweets.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.LinkifiedTextView;
import com.codepath.apps.simpletweets.utils.TweetHelpers;
import com.makeramen.roundedimageview.RoundedImageView;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TweetDetailsActivity extends AppCompatActivity implements
        ComposeTweetDialogFragment.OnComposeDialogFragmentListener {

    private static String EXTRA_TWEET = "com.codepath.apps.simpletweets.activities.tweetdetails";
    private static String EXTRA_CURRENT_USER = "com.codepath.apps.simpletweets.activities.currentuser";

    @Bind(R.id.image_profile) RoundedImageView mProfileImage;
    @Bind(R.id.text_name) TextView mUserName;
    @Bind(R.id.text_screen_name) TextView mScreenName;
    @Bind(R.id.text_body) LinkifiedTextView mTweetBody;
    @Bind(R.id.image_media) ImageView mImageView;
    @Bind(R.id.video_view) VideoView mVideoView;
    @Bind(R.id.button_retweet) Button mRetweetButton;
    @Bind(R.id.button_favorite) Button mFavoritesButton;

    private User mCurrentUser;
    private Tweet mTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTweet = Parcels.unwrap(getIntent().getParcelableExtra(TweetDetailsActivity.EXTRA_TWEET));
        mCurrentUser = Parcels.unwrap(getIntent().getParcelableExtra(TweetDetailsActivity.EXTRA_CURRENT_USER));
        setupTweet();
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

    public static Intent getStartIntent(Context context, Tweet tweet, User currentUser) {
        Intent intent= new Intent(context, TweetDetailsActivity.class);
        intent.putExtra(TweetDetailsActivity.EXTRA_TWEET, Parcels.wrap(tweet));
        intent.putExtra(TweetDetailsActivity.EXTRA_CURRENT_USER, Parcels.wrap(currentUser));
        return intent;
    }

    @Override
    public void onPostedTweet(Tweet newTweetPost) {
        // for now do nothing.
    }

    @OnClick(R.id.button_reply)
    public void onReplyClicked(View view) {
        ComposeTweetDialogFragment tweetDialogFragment = ComposeTweetDialogFragment.newInstance(mCurrentUser, mTweet);
        tweetDialogFragment.show(getSupportFragmentManager(), "fragment_compose_tweet_dialog");
    }

    private void setupVideo() {
        if (TweetHelpers.hasVideo(mTweet)) {
            mVideoView.setVideoPath(mTweet.getVideo().getUrl());
            mVideoView.requestFocus();
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mVideoView.start();
                    mp.setLooping(true);
                }
            });
        } else {
            mVideoView.setVisibility(View.GONE);
        }
    }

    private void setupImage() {
        User user = mTweet.getUser();
        Glide.with(mProfileImage.getContext()).
                load(TweetHelpers.getBestProfilePictureforUser(user)).
                into(mProfileImage);

        if (mTweet.getMedia() != null) {
            Glide.with(mImageView.getContext()).
                    load(mTweet.getMedia().getUrl()).
                    into(mImageView);
        } else {
            mImageView.setVisibility(View.GONE);
        }
    }

    private void setupTweet() {
        User user = mTweet.getUser();
        mUserName.setText(user.getUserName());
        mScreenName.setText(user.getScreenName());
        mTweetBody.setText(mTweet.getText());
        mRetweetButton.setText(Integer.toString(mTweet.getRetweetCount()));
        mFavoritesButton.setText(Integer.toString(mTweet.getFavouritesCount()));

        setupImage();
        setupVideo();
    }
}

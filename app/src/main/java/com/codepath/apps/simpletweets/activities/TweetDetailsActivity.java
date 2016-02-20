package com.codepath.apps.simpletweets.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.LinkifiedTextView;
import com.codepath.apps.simpletweets.utils.TweetHelpers;
import com.makeramen.roundedimageview.RoundedImageView;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TweetDetailsActivity extends AppCompatActivity {

    private static String EXTRA_TWEET = "com.codepath.apps.simpletweets.activities.tweetdetails";
    @Bind(R.id.image_profile) RoundedImageView mProfileImage;
    @Bind(R.id.text_name) TextView mUserName;
    @Bind(R.id.text_screen_name) TextView mScreenName;
    @Bind(R.id.text_body) LinkifiedTextView mTweetBody;
    @Bind(R.id.image_media) ImageView mImageView;
    @Bind(R.id.video_view)
    VideoView mVideoView;

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

    public static Intent getStartIntent(Context context, Tweet tweet) {
        Intent intent= new Intent(context, TweetDetailsActivity.class);
        intent.putExtra(TweetDetailsActivity.EXTRA_TWEET, Parcels.wrap(tweet));
        return intent;
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

        setupImage();
        setupVideo();
    }
}

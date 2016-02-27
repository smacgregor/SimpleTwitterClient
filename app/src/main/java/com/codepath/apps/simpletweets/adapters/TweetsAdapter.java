package com.codepath.apps.simpletweets.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.LinkifiedTextView;
import com.codepath.apps.simpletweets.utils.ParseRelativeDate;
import com.codepath.apps.simpletweets.utils.TweetHelpers;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by smacgregor on 2/17/16.
 */
public class TweetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TWEET = 0;
    private final int VIEW_TWEET_WITH_MEDIA = 1;

    private List<Tweet> mTweets;
    private OnItemClickListener mOnClickListener;

    public TweetsAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(parent.getContext());
        View currentView;
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case VIEW_TWEET_WITH_MEDIA:
                currentView = inflator.inflate(R.layout.item_tweet_with_media, parent, false);
                viewHolder = new TweetViewHolderWithMedia(currentView);
                break;
            default:
                currentView = inflator.inflate(R.layout.item_tweet, parent, false);
                viewHolder = new TweetViewHolder(currentView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TWEET_WITH_MEDIA:
                setupTweetWithMediaViewHolder((TweetViewHolderWithMedia) holder, position);
                break;
            default:
                setupTweetViewHolder((TweetViewHolder) holder, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Tweet tweet = mTweets.get(position);
        return (tweet.getMedia() != null) ? VIEW_TWEET_WITH_MEDIA : VIEW_TWEET;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public void setOnItemClickListener(final OnItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    private void setupTweetViewHolder(TweetViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);
        User user = tweet.getUser();
        holder.mTweetTextBody.setText(tweet.getText());
        holder.mScreenName.setText(user.getScreenName());
        holder.mTimeStamp.setText(ParseRelativeDate.getRelativeTimeAgo(tweet.getCreatedAt()));
        holder.mUserName.setText(user.getUserName());

        int favoritesCount = tweet.getFavoriteCount();
        holder.mFavoritesButton.setText(favoritesCount > 0 ? Integer.toString(favoritesCount) : "");

        int retweetCount= tweet.getRetweetCount();
        holder.mRetweetButton.setText(retweetCount > 0 ? Integer.toString(retweetCount) : "");

        Glide.with(holder.mProfileImage.getContext()).
                load(TweetHelpers.getBestProfilePictureforUser(user)).
                diskCacheStrategy(DiskCacheStrategy.ALL).
                into(holder.mProfileImage);
    }

    private void setupTweetWithMediaViewHolder(TweetViewHolderWithMedia holder, int position) {
        setupTweetViewHolder(holder, position);

        Tweet tweet = mTweets.get(position);
        tweet.getMedia().save();
        // now load the media image
        Glide.with(holder.mMediaImage.getContext()).
                load(tweet.getMedia().getUrl()).
                diskCacheStrategy(DiskCacheStrategy.ALL).
                into(holder.mMediaImage);
    }

    public class TweetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.text_body) LinkifiedTextView mTweetTextBody;
        @Bind(R.id.text_timestamp) TextView mTimeStamp;
        @Bind(R.id.text_screen_name) TextView mScreenName;
        @Bind(R.id.text_name) TextView mUserName;
        @Bind(R.id.image_profile) RoundedImageView mProfileImage;
        @Bind(R.id.button_favorite) Button mFavoritesButton;
        @Bind(R.id.button_retweet) Button mRetweetButton;

        public TweetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        @OnClick({R.id.button_retweet, R.id.button_favorite, R.id.button_reply, R.id.image_profile})
        public void onButtonClicked(View button) {
            // forward this on to our activity to decide what to do
            onClick(button);
        }

        @Override
        public void onClick(View v) {
            if (mOnClickListener != null) {
                mOnClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public class TweetViewHolderWithMedia extends TweetViewHolder {
        @Bind(R.id.image_media) RoundedImageView mMediaImage;
        public TweetViewHolderWithMedia(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        /**
         * An item in the recycler view has been clicked
         * @param view
         * @param position
         */
        void onItemClick(View view, int position);
    }
}

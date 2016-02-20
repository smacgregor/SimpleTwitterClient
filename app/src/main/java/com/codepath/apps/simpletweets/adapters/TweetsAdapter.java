package com.codepath.apps.simpletweets.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.LinkifiedTextView;
import com.codepath.apps.simpletweets.utils.ParseRelativeDate;
import com.codepath.apps.simpletweets.utils.TweetHelpers;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by smacgregor on 2/17/16.
 */
public class TweetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Tweet> mTweets;
    private OnItemClickListener mOnClickListener;

    public TweetsAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(parent.getContext());
        View currentView = inflator.inflate(R.layout.item_tweet, parent, false);
        return new TweetViewHolder(currentView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        setupTweetViewHolder((TweetViewHolder) holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
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

        Picasso.with(holder.mProfileImage.getContext()).
                load(TweetHelpers.getBestProfilePictureforUser(user)).
                into(holder.mProfileImage);
    }

    public class TweetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.text_body) LinkifiedTextView mTweetTextBody;
        @Bind(R.id.text_timestamp) TextView mTimeStamp;
        @Bind(R.id.text_screen_name) TextView mScreenName;
        @Bind(R.id.text_name) TextView mUserName;
        @Bind(R.id.image_profile) RoundedImageView mProfileImage;

        public TweetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnClickListener != null) {
                mOnClickListener.onItemClick(v, getAdapterPosition());
            }
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

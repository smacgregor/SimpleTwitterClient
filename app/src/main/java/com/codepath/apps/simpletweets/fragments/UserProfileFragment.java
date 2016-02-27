package com.codepath.apps.simpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.TweetHelpers;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by smacgregor on 2/26/16.
 */
public class UserProfileFragment extends Fragment {

    private static final String ARGUMENT_USER_ID = "ARGUMENT_USER_ID";

    @Bind(R.id.image_profile) RoundedImageView mProfileImage;
    @Bind(R.id.text_name) TextView mUserName;
    @Bind(R.id.text_screen_name) TextView mScreenName;
    @Bind(R.id.image_profile_background) ImageView mProfileBackground;
    @Bind(R.id.layout_profile_header) RelativeLayout mRelativeLayout;
    @Bind(R.id.text_followers) TextView mFollowersTextView;
    @Bind(R.id.text_following) TextView mFollowingTextView;
    @Bind(R.id.text_user_description) TextView mUserDescription;

    User mUser;

    public static UserProfileFragment newInstance(long userId) {
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putLong(ARGUMENT_USER_ID, userId);
        userProfileFragment.setArguments(args);
        return userProfileFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, view);
        setupUser(mUser);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long userId = getArguments().getLong(ARGUMENT_USER_ID);
        mUser = User.findUser(userId);
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

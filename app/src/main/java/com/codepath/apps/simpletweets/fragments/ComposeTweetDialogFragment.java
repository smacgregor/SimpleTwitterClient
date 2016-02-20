package com.codepath.apps.simpletweets.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.TweetHelpers;
import com.makeramen.roundedimageview.RoundedImageView;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnComposeDialogFragmentListener} interface
 * to handle interaction events.
 * Use the {@link ComposeTweetDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeTweetDialogFragment extends DialogFragment {

    private static final String ARGUMENT_CURRENT_USER = "CURRENT_USER";
    private static final int MAX_TWEET_LENGTH = 140;

    @Bind(R.id.edit_tweet_body) EditText mEditTextField;
    @Bind(R.id.text_tweet_count) TextView mTweetCountTextView;
    @Bind(R.id.image_profile) RoundedImageView mProfileImage;
    @Bind(R.id.button_tweet) Button mPostButton;

    private User mCurrentUser;
    private OnComposeDialogFragmentListener mListener;

    public ComposeTweetDialogFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currentUser The current user
     * @return A new instance of fragment ComposeTweetDialogFragment.
     */
    public static ComposeTweetDialogFragment newInstance(User currentUser) {
        ComposeTweetDialogFragment fragment = new ComposeTweetDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_CURRENT_USER, Parcels.wrap(currentUser));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentUser = Parcels.unwrap(getArguments().getParcelable(ARGUMENT_CURRENT_USER));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose_tweet_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        Glide.with(mProfileImage.getContext()).
                load(TweetHelpers.getBestProfilePictureforUser(mCurrentUser)).
                into(mProfileImage);
        mEditTextField.requestFocus();
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnComposeDialogFragmentListener) {
            mListener = (OnComposeDialogFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnComposeDialogFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnTextChanged(R.id.edit_tweet_body)
    void onTweetBodyTextChange(Editable tweetText) {
        int remainingCharacters = MAX_TWEET_LENGTH - tweetText.toString().length();

        mTweetCountTextView.setText(Integer.toString(remainingCharacters));
        if (remainingCharacters < 0) {
            mTweetCountTextView.setTextColor(ContextCompat.getColor(mTweetCountTextView.getContext(), R.color.red));
        } else {
            mTweetCountTextView.setTextColor(ContextCompat.getColor(mTweetCountTextView.getContext(), R.color.style_color_grey_text));
        }
        mPostButton.setEnabled(remainingCharacters >= 0);
    }

    public interface OnComposeDialogFragmentListener {
        void onPostedTweet(Tweet newTweetPost);
    }
}

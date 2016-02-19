package com.codepath.apps.simpletweets.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

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

    @Bind(R.id.edit_tweet_body) EditText mEditTextField;
    @Bind(R.id.image_profile) RoundedImageView mProfileImage;

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
        Picasso.with(mProfileImage.getContext()).
                load(mCurrentUser.getProfileImageUrl()).
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

    public interface OnComposeDialogFragmentListener {
        void onPostedTweet(Tweet newTweetPost);
    }
}

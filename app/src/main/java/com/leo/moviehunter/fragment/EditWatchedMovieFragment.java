package com.leo.moviehunter.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.leo.moviehunter.R;
import com.leo.moviehunter.util.MHConstants;
import com.leo.moviehunter.util.MHUtil;

import java.util.Locale;

public class EditWatchedMovieFragment extends DialogFragment {
    private static final String TAG = "EditWatchedMovieFragment";

    private static final float DEFAULT_SCORE_SCALE = 0.7f;
    private static final int MAX_STAR_NUMBER = 6;
    private static final int SCORE_STEP = 1;

    private String mMovieId;
    private String mMovieTitle;
    private TextView mScoreText;
    private RatingBar mRatingBar;
    private EditText mCommentText;
    private Dialog mDialog;
    private OnEditDoneListener mOnEditDoneListener;

    public static EditWatchedMovieFragment newInstance(String movieId, String movieTitle) {
        EditWatchedMovieFragment fragment = new EditWatchedMovieFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MHConstants.BUNDLE_KEY_MOVIE_ID, movieId);
        bundle.putString(MHConstants.BUNDLE_KEY_MOVIE_TITLE, movieTitle);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null) {
            mMovieId = getArguments().getString(MHConstants.BUNDLE_KEY_MOVIE_ID);
            mMovieTitle = getArguments().getString(MHConstants.BUNDLE_KEY_MOVIE_TITLE);
        }

        final View customView = getActivity().getLayoutInflater().inflate(R.layout.fragment_edit_watched_movie, null);
        mScoreText = (TextView) customView.findViewById(R.id.score);
        mRatingBar = (RatingBar) customView.findViewById(R.id.rating);
        mCommentText = (EditText) customView.findViewById(R.id.comment);

        mRatingBar.setNumStars(MAX_STAR_NUMBER);
        mRatingBar.setStepSize(score2Star(SCORE_STEP));
        mRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mScoreText.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.rating), star2Score(rating)));
            }
        });
        mRatingBar.setRating(score2Star(Math.round(MHConstants.MAX_MOVIE_SCORE * DEFAULT_SCORE_SCALE)));

        mDialog = new AlertDialog.Builder(getActivity())
                .setTitle(String.format(Locale.getDefault(), "%s [%s]", getString(R.string.save), mMovieTitle))
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mOnEditDoneListener.onEditDone(mMovieId, star2Score(mRatingBar.getRating()), mCommentText.getText().toString());
                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel, null)
                .setView(customView)
                .create();
        return mDialog;
    }

    public interface OnEditDoneListener {
        void onEditDone(String movieId, float score, String comment);
    }

    public void setOnEditDoneListener(OnEditDoneListener listener) {
        mOnEditDoneListener = listener;
    }

    private static float score2Star(int score) {
        return MHUtil.score2Star(score, MAX_STAR_NUMBER);
    }

    private static int star2Score(float star) {
        return (int) MHUtil.star2Score(star, MAX_STAR_NUMBER);
    }
}

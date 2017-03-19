package com.leo.moviehunter.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leo.moviehunter.R;
import com.leo.moviehunter.data.Movie;
import com.leo.moviehunter.data.user.WatchItem;
import com.leo.moviehunter.util.MHConstants;
import com.leo.moviehunter.util.MHUtil;

import java.util.Locale;

public class EditWatchedMovieFragment extends DialogFragment {
    private static final String TAG = "EditWatchedMovieFragment";

    private static final float DEFAULT_SCORE_SCALE = 0.7f;
    private static final int MAX_STAR_NUMBER = 6;
    private static final int SCORE_STEP = 1;

    private Movie mMovie;
    private WatchItem mWatchItem;
    private TextView mTitle;
    private TextView mScoreText;
    private RatingBar mRatingBar;
    private EditText mCommentText;
    private Dialog mDialog;
    private OnEditDoneListener mOnEditDoneListener;

    public static EditWatchedMovieFragment newInstance(@NonNull Movie movie, @Nullable WatchItem watchItem) {
        EditWatchedMovieFragment fragment = new EditWatchedMovieFragment();
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putString(MHConstants.BUNDLE_KEY_MOVIE_JSON, gson.toJson(movie));
        if (watchItem != null) {
            bundle.putString(MHConstants.BUNDLE_KEY_WATCH_ITEM_JSON, gson.toJson(watchItem));
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null) {
            Gson gson = new Gson();
            mMovie = gson.fromJson(getArguments().getString(MHConstants.BUNDLE_KEY_MOVIE_JSON), Movie.class);
            if (getArguments().containsKey(MHConstants.BUNDLE_KEY_WATCH_ITEM_JSON)) {
                mWatchItem = gson.fromJson(getArguments().getString(MHConstants.BUNDLE_KEY_WATCH_ITEM_JSON), WatchItem.class);
            }
        }

        final View customView = getActivity().getLayoutInflater().inflate(R.layout.fragment_edit_watched_movie, null);
        mTitle = (TextView) customView.findViewById(R.id.title);
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

        // giving value
        final float score;
        final String comment;
        final int titleRes;
        if (WatchItem.isWatched(mWatchItem)) {
            score = mWatchItem.getScore();
            comment = mWatchItem.getComment();
            titleRes = R.string.edit_watched_movie;
        } else {
            score = MHConstants.MAX_MOVIE_SCORE * DEFAULT_SCORE_SCALE;
            comment = "";
            titleRes = R.string.save_to_watched_movie;
        }
        mTitle.setText(mMovie.getTitle());
        mRatingBar.setRating(score2Star(Math.round(score)));
        mCommentText.setText(comment);

        mDialog = new AlertDialog.Builder(getActivity())
                .setTitle(titleRes)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mOnEditDoneListener.onEditDone(mMovie.getId(), star2Score(mRatingBar.getRating()), mCommentText.getText().toString());
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

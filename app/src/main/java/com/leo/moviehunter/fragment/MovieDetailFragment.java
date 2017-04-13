package com.leo.moviehunter.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.leo.moviehunter.R;
import com.leo.moviehunter.data.Movie;
import com.leo.moviehunter.data.user.WatchItem;
import com.leo.moviehunter.fragment.EditWatchedMovieFragment.OnEditDoneListener;
import com.leo.moviehunter.task.AddToWatchedListTask;
import com.leo.moviehunter.task.GetImageBaseUrlTask;
import com.leo.moviehunter.util.CommonUtil;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MHConstants;
import com.leo.moviehunter.util.MHUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailFragment extends Fragment {
    private static final String TAG = "MovieDetailFragment";

    private final DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @BindView(R.id.image) ImageView mImage;
    @BindView(R.id.text1) TextView mText1;
    @BindView(R.id.text2) TextView mText2;
    @BindView(R.id.text3) TextView mText3;
    @BindView(R.id.text4) TextView mText4;
    @BindView(R.id.content) TextView mTextContent;
    @BindView(R.id.rating) TextView mRating;
    @BindView(R.id.date) TextView mDate;
    @BindView(R.id.comment) TextView mComment;
    @BindView(R.id.icon_edit) ImageView mEditIcon;
    @BindView(R.id.watched_container) ViewGroup mWatchedContainer;

    private Movie mMovie;
    private WatchItem mWatchItem;
    private String mBaseImageUrl;
    private boolean mViewReady;

    public static MovieDetailFragment newInstance(@NonNull Movie movie, @Nullable WatchItem watchItem) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putString(MHConstants.BUNDLE_KEY_MOVIE_JSON, gson.toJson(movie));
        if (watchItem != null) {
            bundle.putParcelable(MHConstants.BUNDLE_KEY_WATCH_ITEM, watchItem);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Gson gson = new Gson();
            mMovie = gson.fromJson(getArguments().getString(MHConstants.BUNDLE_KEY_MOVIE_JSON), Movie.class);
            if (getArguments().containsKey(MHConstants.BUNDLE_KEY_WATCH_ITEM)) {
                mWatchItem = getArguments().getParcelable(MHConstants.BUNDLE_KEY_WATCH_ITEM);
            }
        }

        // load image base url
        new GetImageBaseUrlTask() {
            @Override
            public void onGetUrl(String url) {
                mBaseImageUrl = url;
                setByMovie();
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View root = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, root);

        mViewReady = true;
        setByMovie();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        MHUtil.setActivityToolbarSubTitle(getActivity(), "");
    }

    @UiThread
    private void setByMovie() {
        if (!mViewReady) {
            return;
        }

        final Movie movie = mMovie;
        if (movie == null) {
            return;
        }

        if (!TextUtils.isEmpty(mBaseImageUrl)) {
            Glide.with(this)
                    .load(mBaseImageUrl + movie.getCoverImageUrl())
                    .placeholder(android.R.drawable.ic_dialog_alert)
                    .centerCrop()
                    .crossFade()
                    .into(mImage);
        }
        mText1.setText(movie.getTitle());
        mText2.setText(movie.getOriginalTitle());
        mText3.setText(movie.getReleaseDate());
        mText4.setText(getString(R.string.score) + ": " + movie.getScore());
        mTextContent.setText(movie.getOverview());

        if (WatchItem.isWatched(mWatchItem)) {
            mWatchedContainer.setVisibility(View.VISIBLE);
            updateWatchItemContent();

            mEditIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditWatchedMovieFragment fragment = EditWatchedMovieFragment.newInstance(movie, mWatchItem);
                    fragment.setOnEditDoneListener(new OnEditDoneListener() {
                        @Override
                        public void onEditDone(String movieId, float score, long watchDate, String comment) {
                            Log.d(TAG, "onEditDone() - movieId: " + movieId + ", score: " + score + ", watchDate: " + mDateFormat.format(watchDate) + ", comment: " + comment);

                            mWatchItem.setScore(score);
                            mWatchItem.setWatchedEpochTime(watchDate);
                            mWatchItem.setComment(comment);
                            ArrayList<WatchItem> list = new ArrayList<>();
                            list.add(mWatchItem);
                            new AddToWatchedListTask(getActivity()) {
                                @Override
                                protected void onDone(List<WatchItem> addedList) {
                                }
                            }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, list);

                            updateWatchItemContent();
                        }
                    });
                    fragment.show(getFragmentManager(), null);
                }
            });
        } else {
            mWatchedContainer.setVisibility(View.GONE);
        }
    }

    @UiThread
    private void updateWatchItemContent() {
        mRating.setText(CommonUtil.toHtmlColorSpanned("red", String.valueOf(mWatchItem.getScore())));
        mDate.setText(CommonUtil.toHtmlColorSpanned("red", mDateFormat.format(mWatchItem.getWatchedEpochTime())));
        mComment.setText(CommonUtil.toHtmlColorSpanned("red", mWatchItem.getComment()));
    }
}

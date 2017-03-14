package com.leo.moviehunter.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leo.moviehunter.R;
import com.leo.moviehunter.data.Movie;
import com.leo.moviehunter.task.GetMovieDetailTask;
import com.leo.moviehunter.task.GetImageBaseUrlTask;
import com.leo.moviehunter.util.CommonUtil;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MHConstants;

public class MovieDetailFragment extends Fragment {
    private static final String TAG = "MovieDetailFragment";

    private ImageView mImage;
    private TextView mText1;
    private TextView mText2;
    private TextView mText3;
    private TextView mText4;
    private TextView mTextContent;

    private Movie mMovie;
    private String mMovieId;
    private String mBaseImageUrl;
    private boolean mViewReady;

    public static MovieDetailFragment newInstance(String movieId) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MHConstants.BUNDLE_KEY_MOVIE_ID, movieId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMovieId = getMovieId();
        Log.d(TAG, "mMovieId: " + mMovieId);
        if (TextUtils.isEmpty(mMovieId)) {
            return;
        }

        // load image base url
        new GetImageBaseUrlTask() {
            @Override
            public void onGetUrl(String url) {
                mBaseImageUrl = url;
                Log.d(TAG, "mBaseImageUrl: " + mBaseImageUrl);
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        new GetMovieDetailTask(getActivity(), mMovieId) {
            @Override
            protected void onGetMovie(Movie movie) {
                mMovie = movie;
                setByMovie();
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View root = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mImage = (ImageView) root.findViewById(R.id.image);
        mText1 = (TextView) root.findViewById(R.id.text1);
        mText2 = (TextView) root.findViewById(R.id.text2);
        mText3 = (TextView) root.findViewById(R.id.text3);
        mText4 = (TextView) root.findViewById(R.id.text4);
        mTextContent = (TextView) root.findViewById(R.id.content);

        mViewReady = true;
        setByMovie();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        CommonUtil.setActivityToolbarSubTitle(getActivity(), "");
    }

    private String getMovieId() {
        return getArguments() != null ? getArguments().getString(MHConstants.BUNDLE_KEY_MOVIE_ID) : null;
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

        Glide.with(this)
                .load(mBaseImageUrl + movie.getCoverImageUrl())
                .placeholder(android.R.drawable.ic_dialog_alert)
                .centerCrop()
                .crossFade()
                .into(mImage);
        mText1.setText(movie.getTitle());
        mText2.setText(movie.getOriginalTitle());
        mText3.setText(movie.getReleaseDate());
        mText4.setText(getString(R.string.score) + ": " + movie.getScore());
        mTextContent.setText(movie.getOverview());
    }
}

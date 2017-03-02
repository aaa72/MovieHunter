package com.leo.moviehunter.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leo.moviehunter.R;
import com.leo.moviehunter.tmdb.response.MovieDetail;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.util.GetImgeBaseUrlTask;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.MHConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailFragment extends Fragment {
    private static final String TAG = "MovieDetailFragment";

    private ImageView mImage;
    private TextView mText1;
    private TextView mText2;
    private TextView mText3;
    private TextView mText4;
    private TextView mTextContent;

    private MovieDetail mMovieDetail;
    private int mMovieId = -1;
    private String mBaseImageUrl;
    private boolean mViewReady;

    public static MovieDetailFragment newInstance(int movieId) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MHConstants.BUNDLE_KEY_MOVIE_ID, movieId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMovieId = getMovieId();
        Log.d(TAG, "mMovieId: " + mMovieId);

        // load image base url
        new GetImgeBaseUrlTask() {
            @Override
            public void onGetUrl(String url) {
                mBaseImageUrl = url;
                Log.d(TAG, "mBaseImageUrl: " + mBaseImageUrl);

                TMDBServiceManager.getTMDBService().getMovieDetail(mMovieId).enqueue(new Callback<MovieDetail>() {
                    @Override
                    public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                        if (response.isSuccessful()) {
                            mMovieDetail = response.body();
                            setByMovie();
                        } else {
                            Log.w(TAG, "getMovieDetail fail by code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieDetail> call, Throwable t) {
                        Log.w(TAG, "getMovieDetail onFailure", t);
                    }
                });
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

    private int getMovieId() {
        return getArguments() != null ? getArguments().getInt(MHConstants.BUNDLE_KEY_MOVIE_ID) : -1;
    }

    @UiThread
    private void setByMovie() {
        if (!mViewReady) {
            return;
        }

        final MovieDetail movie = mMovieDetail;
        if (movie == null) {
            return;
        }

        Glide.with(this)
                .load(mBaseImageUrl + movie.poster_path)
                .placeholder(android.R.drawable.ic_dialog_alert)
                .centerCrop()
                .crossFade()
                .into(mImage);
        mText1.setText(movie.title);
        mText2.setText(movie.original_title);
        mText3.setText(movie.release_date);
        mText4.setText(getString(R.string.score) + ": " + movie.vote_average);
        mTextContent.setText(movie.overview);
    }
}

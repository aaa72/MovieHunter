package com.leo.moviehunter.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = "MovieDetailActivity";

    private ImageView mImage;
    private TextView mText1;
    private TextView mText2;
    private TextView mText3;
    private TextView mText4;
    private TextView mTextContent;

    private int mMovieId = -1;
    private String mBaseImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_movie_detail);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mImage = (ImageView) findViewById(R.id.image);
        mText1 = (TextView) findViewById(R.id.text1);
        mText2 = (TextView) findViewById(R.id.text2);
        mText3 = (TextView) findViewById(R.id.text3);
        mText4 = (TextView) findViewById(R.id.text4);
        mTextContent = (TextView) findViewById(R.id.content);

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
                            MovieDetail movie = response.body();
                            setByMovie(movie);
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

    private int getMovieId() {
        return getIntent() != null ? getIntent().getIntExtra(MHConstants.BUNDLE_KEY_MOVIE_ID, -1) : -1;
    }

    @UiThread
    private void setByMovie(MovieDetail movie) {
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

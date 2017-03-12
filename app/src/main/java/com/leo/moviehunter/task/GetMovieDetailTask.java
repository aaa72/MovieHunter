package com.leo.moviehunter.task;

import android.os.AsyncTask;

import com.leo.moviehunter.tmdb.response.MovieDetail;
import com.leo.moviehunter.tmdb.response.NowPlaying;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class GetMovieDetailTask extends AsyncTask<Void, Void, MovieDetail> {
    private static final String TAG = "GetMovieDetailTask";

    private final String mMovieId;

    public GetMovieDetailTask(String movieId) {
        mMovieId = movieId;
    }


    @Override
    protected MovieDetail doInBackground(Void... params) {

        Call<MovieDetail> call = TMDBServiceManager.getTMDBService().getMovieDetail(Integer.parseInt(mMovieId));

        try {
            Response<MovieDetail> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.w(TAG, "getMovieDetail fail by code: " + response.code());
            }
        } catch (IOException e) {
            Log.w(TAG, "getMovieDetail fail", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(MovieDetail movieDetail) {
        onGetMovieDetail(movieDetail);
    }

    abstract protected void onGetMovieDetail(MovieDetail movieDetail);
}

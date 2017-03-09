package com.leo.moviehunter.task;

import android.os.AsyncTask;

import com.leo.moviehunter.tmdb.TMDBConstants;
import com.leo.moviehunter.tmdb.response.DiscoverMovie;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class DiscoverMoreMovieTask extends AsyncTask<Integer, Void, DiscoverMovie> {
    private static final String TAG = "DiscoverMoreMovieTask";

    private final int mGenreId;

    public DiscoverMoreMovieTask(int genreId) {
        mGenreId = genreId;
    }

    @Override
    protected DiscoverMovie doInBackground(Integer... params) {
        Call<DiscoverMovie> call = TMDBServiceManager.getTMDBService().discoverMovie(
                TMDBConstants.SortBy.popularity.desc()
                , true
                , params[0]
                , String.valueOf(mGenreId)
        );
        try {
            Response<DiscoverMovie> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.w(TAG, "discoverMovie fail by code: " + response.code());
            }
        } catch (IOException e) {
            Log.w(TAG, "discoverMovie fail", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(DiscoverMovie discoverMovie) {
        onGetDiscoverMovie(discoverMovie);
    }

    abstract protected void onGetDiscoverMovie(DiscoverMovie discoverMovie);
}

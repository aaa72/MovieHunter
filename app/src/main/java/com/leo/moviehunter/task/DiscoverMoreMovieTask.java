package com.leo.moviehunter.task;

import android.os.AsyncTask;

import com.leo.moviehunter.data.Movie;
import com.leo.moviehunter.tmdb.TMDBConstants;
import com.leo.moviehunter.tmdb.response.DiscoverMovie;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.TMDBUtil;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class DiscoverMoreMovieTask extends AsyncTask<Integer, Void, DiscoverMovie> {
    private static final String TAG = "DiscoverMoreMovieTask";

    private final String mGenreId;

    public DiscoverMoreMovieTask(String genreId) {
        mGenreId = genreId;
    }

    @Override
    protected DiscoverMovie doInBackground(Integer... params) {
        Call<DiscoverMovie> call = TMDBServiceManager.getTMDBService().discoverMovie(
                TMDBConstants.SortBy.popularity.desc()
                , true
                , params[0]
                , mGenreId
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
        if (discoverMovie == null || discoverMovie.results == null) {
            onFail(0, "");
            return;
        }
        Movie[] movies = TMDBUtil.movieResults2Movies(discoverMovie.results);
        onGetDiscoverMovie(movies, discoverMovie.total_results, discoverMovie.page, discoverMovie.total_pages);
    }

    abstract protected void onGetDiscoverMovie(Movie[] movies, int totalMovies, int page, int totalPages);
    abstract protected void onFail(int code, String msg);
}

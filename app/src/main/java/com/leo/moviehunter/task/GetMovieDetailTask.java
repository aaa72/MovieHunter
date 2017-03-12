package com.leo.moviehunter.task;

import android.content.Context;
import android.os.AsyncTask;

import com.leo.moviehunter.data.Movie;
import com.leo.moviehunter.tmdb.response.MovieDetail;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.util.CacheManager;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.TMDBUtil;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class GetMovieDetailTask extends AsyncTask<Void, Void, Movie> {
    private static final String TAG = "GetMovieDetailTask";

    private final Context mContext;
    private final String mMovieId;

    public GetMovieDetailTask(Context context, String movieId) {
        mContext = context.getApplicationContext();
        mMovieId = movieId;
    }

    @Override
    protected Movie doInBackground(Void... params) {
        DualCache<Movie> cache = CacheManager.getInstance(mContext).getCache(Movie.class);

        synchronized (cache) {
            if (cache.contains(mMovieId)) {
                return cache.get(mMovieId);
            }

            Movie movie = null;

            Call<MovieDetail> call = TMDBServiceManager.getTMDBService().getMovieDetail(Integer.parseInt(mMovieId));
            try {
                Response<MovieDetail> response = call.execute();
                if (response.isSuccessful()) {
                    movie = TMDBUtil.movieDetail2Movie(response.body());
                } else {
                    Log.w(TAG, "getMovieDetail fail by code: " + response.code());
                }
            } catch (IOException e) {
                Log.w(TAG, "getMovieDetail fail", e);
            }

            if (movie != null) {
                cache.put(movie.getId(), movie);
            }
            return movie;
        }
    }

    @Override
    protected void onPostExecute(Movie movie) {
        onGetMovie(movie);
    }

    abstract protected void onGetMovie(Movie movie);
}

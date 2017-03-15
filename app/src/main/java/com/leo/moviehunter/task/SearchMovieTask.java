package com.leo.moviehunter.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.leo.moviehunter.data.Movie;
import com.leo.moviehunter.task.SearchMovieTask.Params;
import com.leo.moviehunter.tmdb.response.SearchMovie;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.TMDBUtil;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class SearchMovieTask extends AsyncTask<Params, Void, SearchMovie> {
    private static final String TAG = "SearchMovieTask";

    private Params mParams;

    public class Params {
        String mSearchString;
        int mPage;

        public Params(String searchString, int page) {
            mSearchString = searchString;
            mPage = page;
        }
    }

    @Override
    protected SearchMovie doInBackground(Params... params) {
        mParams = params[0];
        if (TextUtils.isEmpty(mParams.mSearchString)) {
            return null;
        }

        Call<SearchMovie> call = TMDBServiceManager.getTMDBService().searchMovie(mParams.mSearchString, mParams.mPage, true, null);
        try {
            Response<SearchMovie> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.w(TAG, "getNowPlaying fail by code: " + response.code());
            }
        } catch (IOException e) {
            Log.w(TAG, "getNowPlaying fail", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(SearchMovie searchMovie) {
        if (searchMovie == null) {
            onFail(0, "");
            return;
        }
        Movie[] movies = TMDBUtil.movieResults2Movies(searchMovie.results);
        onSearchMovie(mParams.mSearchString, movies, searchMovie.total_results, searchMovie.page, searchMovie.total_pages);
    }

    abstract protected void onSearchMovie(String searchString, Movie[] movies, int totalMovies, int page, int totalPages);
    abstract protected void onFail(int code, String msg);
}

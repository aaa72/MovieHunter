package com.leo.moviehunter.task;

import android.os.AsyncTask;

import com.leo.moviehunter.data.Movie;
import com.leo.moviehunter.tmdb.response.NowPlaying;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.util.Log;
import com.leo.moviehunter.util.TMDBUtil;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class GetNowPlayingTask extends AsyncTask<Integer, Void, NowPlaying> {
    private static final String TAG = "GetNowPlayingTask";

    @Override
    protected NowPlaying doInBackground(Integer... params) {
        Call<NowPlaying> call = TMDBServiceManager.getTMDBService().getNowPlaying(params[0]);
        try {
            Response<NowPlaying> response = call.execute();
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
    protected void onPostExecute(NowPlaying nowPlaying) {
        if (nowPlaying == null) {
            onFail(0, "");
            return;
        }
        Movie[] movies = TMDBUtil.movieResults2Movies(nowPlaying.results);
        onGetNowPlaying(movies, nowPlaying.total_results, nowPlaying.page, nowPlaying.total_pages);
    }


    abstract protected void onGetNowPlaying(Movie[] movies, int totalMovies, int page, int totalPages);
    abstract protected void onFail(int code, String msg);
}

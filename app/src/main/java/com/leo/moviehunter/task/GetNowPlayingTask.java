package com.leo.moviehunter.task;

import android.os.AsyncTask;

import com.leo.moviehunter.tmdb.response.NowPlaying;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.util.Log;

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
        onGetNowPlaying(nowPlaying);
    }


    abstract protected void onGetNowPlaying(NowPlaying nowPlaying);
}

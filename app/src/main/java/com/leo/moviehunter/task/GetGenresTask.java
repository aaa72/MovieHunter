package com.leo.moviehunter.task;

import android.os.AsyncTask;

import com.leo.moviehunter.tmdb.response.GetGenres;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.util.Log;

import retrofit2.Call;
import retrofit2.Response;

public abstract class GetGenresTask extends AsyncTask<Void, Void, GetGenres> {
    private static final String TAG = "GetGenresTask";

    @Override
    protected GetGenres doInBackground(Void ...aVoid) {

        Call<GetGenres> call = TMDBServiceManager.getTMDBService().getGenres();

        try {
            Response<GetGenres> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.w(TAG, "getGenres fail");
            }
        } catch (Exception e) {
            Log.w(TAG, "getGenres fail", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(GetGenres getGenres) {
        getGenres(getGenres);
    }

    abstract protected void getGenres(GetGenres getGenres);
}

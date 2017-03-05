package com.leo.moviehunter.util;

import android.content.Context;
import android.os.AsyncTask;

import com.leo.moviehunter.tmdb.TMDBConstants;
import com.leo.moviehunter.tmdb.response.DiscoverMovie;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;

import retrofit2.Call;
import retrofit2.Response;

public abstract class GetGenreCoverUrlTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "GetGenreCoverUrlTask";

    private GenreCoverUrlCache mUrlCache;
    private int mGenreId;
    private SimpleIdlingResource mSimpleIdlingResource;

    public GetGenreCoverUrlTask(Context context, int genreId) {
        mUrlCache = GenreCoverUrlCache.getInstance(context);
        mGenreId = genreId;
        mSimpleIdlingResource = new SimpleIdlingResource();
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute() - mGenreId: " + mGenreId);
        // main thread operation
        String url = mUrlCache.getCoverUrl(mGenreId);
        if (url != null) {
            Log.d(TAG, "onPreExecute() - cache url: " + url);
            cancel(false /* meaningless */);
            onGetUrl(mGenreId, url);
        } else {
            mSimpleIdlingResource.setIdleState(false);
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.d(TAG, "doInBackground");
        Call<DiscoverMovie> call = TMDBServiceManager.getTMDBService().discoverMovie(
                TMDBConstants.SortBy.popularity.desc(), true, 1, String.valueOf(mGenreId));
        try {
            Response<DiscoverMovie> response = call.execute();
            if (response.isSuccessful()) {
                return response.body().results[0].poster_path;
            } else {
                Log.w(TAG, "GetGenreCoverUrlTask fail");
            }
        } catch (Exception e) {
            Log.w(TAG, "GetGenreCoverUrlTask fail", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String url) {
        Log.d(TAG, "onPostExecute() - url = " + url);
        if (url != null) {
            mUrlCache.putCoverUrl(mGenreId, url);
        }
        onGetUrl(mGenreId, url);

        mSimpleIdlingResource.setIdleState(false);
    }

    abstract public void onGetUrl(int genreId, String url);
}

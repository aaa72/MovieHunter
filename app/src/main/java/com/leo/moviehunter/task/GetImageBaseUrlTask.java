package com.leo.moviehunter.task;

import android.os.AsyncTask;

import com.leo.moviehunter.tmdb.service.TMDBConfiguration;

public abstract class GetImageBaseUrlTask extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
        return TMDBConfiguration.getImageBaseUrl();
    }

    @Override
    protected void onPostExecute(String imageBaseUrl) {
        onGetUrl(imageBaseUrl);
    }

    abstract protected void onGetUrl(String url);
}

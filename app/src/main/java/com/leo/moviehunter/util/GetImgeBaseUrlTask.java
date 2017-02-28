package com.leo.moviehunter.util;

import android.os.AsyncTask;

import com.leo.moviehunter.tmdb.service.TMDBConfiguration;

public abstract class GetImgeBaseUrlTask extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
        return TMDBConfiguration.getImageBaseUrl();
    }

    @Override
    protected void onPostExecute(String imageBaseUrl) {
        onGetUrl(imageBaseUrl);
    }

    abstract public void onGetUrl(String url);
}

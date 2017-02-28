package com.leo.moviehunter.tmdb.service;

import com.leo.moviehunter.tmdb.response.GetConfiguration;
import com.leo.moviehunter.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class TMDBConfiguration {
    private static final String TAG = "TMDBConfiguration";

    private static GetConfiguration mConfiguration;

    public static synchronized GetConfiguration getConfiguration() throws IOException {
        if (mConfiguration !=  null) {
           return mConfiguration;
        }

        Call<GetConfiguration> call = TMDBServiceManager.getTMDBService().getConfiguration();
        Response<GetConfiguration> response = call.execute();
        if (response.isSuccessful()) {
            return mConfiguration = response.body();
        } else {
            throw new IOException("response fail with code:" + response.code());
        }
    }

    public static String getImageBaseUrl() {
        try {
            GetConfiguration config = getConfiguration();
            return config.images.base_url + config.images.backdrop_sizes[0] +"/";
        } catch (Exception e) {
            Log.w(TAG, "get base url fail", e);
        }
        return null;
    }
}

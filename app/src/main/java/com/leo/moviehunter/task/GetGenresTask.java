package com.leo.moviehunter.task;

import android.content.Context;
import android.os.AsyncTask;

import com.leo.moviehunter.data.Genre;
import com.leo.moviehunter.tmdb.TMDBConstants;
import com.leo.moviehunter.tmdb.response.DiscoverMovie;
import com.leo.moviehunter.tmdb.response.GetGenres;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;
import com.leo.moviehunter.util.CacheManager;
import com.leo.moviehunter.util.Log;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;

import retrofit2.Call;
import retrofit2.Response;

public abstract class GetGenresTask extends AsyncTask<Void, Void, Genre[]> {
    private static final String TAG = "GetGenresTask";
    private static final String GERRE_CACHE_KEY = "genre_cache_key";

    private final Context mContext;

    public GetGenresTask (Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    protected Genre[] doInBackground(Void ...aVoid) {
        DualCache<Genre[]> cache = CacheManager.getInstance(mContext).getCache(Genre[].class);

        if (cache.contains(GERRE_CACHE_KEY)) {
            return cache.get(GERRE_CACHE_KEY);
        }

        Call<GetGenres> call = TMDBServiceManager.getTMDBService().getGenres();

        GetGenres getGenres = null;
        try {
            Response<GetGenres> response = call.execute();
            if (response.isSuccessful()) {
                getGenres = response.body();
            } else {
                Log.w(TAG, "getGenres fail");
            }
        } catch (Exception e) {
            Log.w(TAG, "getGenres fail", e);
        }

        com.leo.moviehunter.tmdb.response.Genre[] tmdbGenres;
        if (getGenres == null || (tmdbGenres = getGenres.genres) == null) {
            return null;
        }

        Genre[] genres = new Genre[tmdbGenres.length];
        for (int i = 0; i < tmdbGenres.length; i++) {
            genres[i] = new Genre();
            genres[i].setId(String.valueOf(tmdbGenres[i].id));
            genres[i].setName(tmdbGenres[i].name);

            Call<DiscoverMovie> callDiscover = TMDBServiceManager.getTMDBService().discoverMovie(
                    TMDBConstants.SortBy.popularity.desc(), true, 1, genres[i].getId());
            try {
                Response<DiscoverMovie> response = callDiscover.execute();
                if (response.isSuccessful()) {
                    genres[i].setCoverImageUrl(response.body().results[0].poster_path);
                } else {
                    Log.w(TAG, "discoverMovie fail");
                }
            } catch (Exception e) {
                Log.w(TAG, "discoverMovie fail", e);
            }
        }

        cache.put(GERRE_CACHE_KEY, genres);

        return genres;
    }

    @Override
    protected void onPostExecute(Genre[] genres) {
        getGenres(genres);
    }

    abstract protected void getGenres(Genre[] genres);
}

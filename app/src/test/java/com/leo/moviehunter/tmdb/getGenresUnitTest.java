package com.leo.moviehunter.tmdb;

import com.leo.moviehunter.tmdb.response.Genre;
import com.leo.moviehunter.tmdb.response.Genres;
import com.leo.moviehunter.tmdb.service.TMDBService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class getGenresUnitTest {

    private TMDBService mService;

    @Before
    public void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDBConstants.TMDB_API_3_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mService = retrofit.create(TMDBService.class);
    }

    @Test
    public void searchMovie() throws Exception {
        System.out.println("start test");

        Call<Genres> call = mService.getGenres(TMDBConstants.TMDB_API_KEY, Locale.getDefault().getLanguage());

        Response<Genres> response = call.execute();

        System.out.println("response successful: " + response.isSuccessful());

        Genres genres = response.body();
        for (Genre genre : genres.genres) {
            System.out.println("id: " + genre.id + ", name: " + genre.name);
        }

        Assert.assertTrue(response.isSuccessful());
    }
}



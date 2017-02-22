package com.leo.moviehunter.tmdb;

import com.leo.moviehunter.tmdb.TMDBConstants.SortBy;
import com.leo.moviehunter.tmdb.response.DiscoverMovie;
import com.leo.moviehunter.tmdb.response.DiscoverMovie.Result;
import com.leo.moviehunter.tmdb.service.TMDBService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class discoverMovieUnitTest {

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

        Call<DiscoverMovie> call = mService.discoverMovie(TMDBConstants.TMDB_API_KEY
                , Locale.getDefault().getLanguage(), Locale.getDefault().getCountry()
                , SortBy.popularity.asc(), true, 1, "");

        Response<DiscoverMovie> response = call.execute();

        System.out.println("response successful: " + response.isSuccessful());

        DiscoverMovie discoverMovie = response.body();
        System.out.println(discoverMovie.page + "/" + discoverMovie.total_pages);
        for (Result result : discoverMovie.results) {
            System.out.println("title: " + result.title + ", ori title: " + result.original_title);
        }

        Assert.assertTrue(response.isSuccessful());
    }
}



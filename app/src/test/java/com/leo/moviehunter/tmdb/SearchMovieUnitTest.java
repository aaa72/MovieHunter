package com.leo.moviehunter.tmdb;

import com.leo.moviehunter.tmdb.response.SearchMovie;
import com.leo.moviehunter.tmdb.response.SearchMovie.Results;
import com.leo.moviehunter.tmdb.service.TMDBService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchMovieUnitTest {

    public TMDBService mService;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mService = retrofit.create(TMDBService.class);
    }

    @Test
    public void searchMovie() throws Exception {
        System.out.println("start test");

        Call<SearchMovie> call = mService.searchMovie(Constants.TMDB_API_KEY, "Jack ", "en", 1, false, 2016);

        Response<SearchMovie> response = call.execute();

        System.out.println("response successful: " + response.isSuccessful());

        SearchMovie searchMovie = response.body();
        for (Results result : searchMovie.results) {
            System.out.println("title: " + result.title + ", overview: " + result.overview);
        }

        exception.expect(IllegalArgumentException.class);
        throw new IllegalArgumentException();
    }
}



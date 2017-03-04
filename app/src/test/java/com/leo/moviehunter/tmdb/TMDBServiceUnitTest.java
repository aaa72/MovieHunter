package com.leo.moviehunter.tmdb;

import com.google.gson.Gson;
import com.leo.moviehunter.tmdb.response.DiscoverMovie;
import com.leo.moviehunter.tmdb.response.Genre;
import com.leo.moviehunter.tmdb.response.GetConfiguration;
import com.leo.moviehunter.tmdb.response.GetGenres;
import com.leo.moviehunter.tmdb.response.MovieDetail;
import com.leo.moviehunter.tmdb.response.MovieResult;
import com.leo.moviehunter.tmdb.response.NowPlaying;
import com.leo.moviehunter.tmdb.response.SearchMovie;
import com.leo.moviehunter.tmdb.service.TMDBServiceManager;

import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

import static org.junit.Assert.assertTrue;

public class TMDBServiceUnitTest {

    private TMDBServiceManager.TMDBService mService;

    @Before
    public void init() {
        mService = TMDBServiceManager.getTMDBService();
    }

    @Test
    public void searchMovie() throws Exception {
        System.out.println("searchMovie");

        Call<SearchMovie> call = mService.searchMovie("Jack ", 1, false, 2016);

        Response<SearchMovie> response = call.execute();

        System.out.println("response successful: " + response.isSuccessful());

        SearchMovie searchMovie = response.body();
        for (MovieResult result : searchMovie.results) {
            System.out.println("title: " + result.title + ", overview: " + result.overview);
        }

        assertTrue(response.isSuccessful());
    }

    @Test
    public void getGenres() throws Exception {
        System.out.println("getGenres");

        Call<GetGenres> call = mService.getGenres();

        Response<GetGenres> response = call.execute();

        System.out.println("response successful: " + response.isSuccessful());

        GetGenres genres = response.body();
        for (Genre genre : genres.genres) {
            System.out.println("id: " + genre.id + ", name: " + genre.name);
        }

        assertTrue(response.isSuccessful());
    }

    @Test
    public void discoverMovie() throws Exception {
        System.out.println("discoverMovie");

        Call<DiscoverMovie> call = mService.discoverMovie(TMDBConstants.SortBy.popularity.desc(), true, 1, "53");

        Response<DiscoverMovie> response = call.execute();

        System.out.println("response successful: " + response.isSuccessful());

        DiscoverMovie discoverMovie = response.body();
        System.out.println(discoverMovie.page + "/" + discoverMovie.total_pages);
        for (MovieResult result : discoverMovie.results) {
            System.out.println("id: " + result.id + ", title: " + result.title + ", ori title: " + result.original_title);
        }

        assertTrue(response.isSuccessful());
    }

    @Test
    public void getConfiguration() throws Exception {
        System.out.println("getConfiguration");

        Call<GetConfiguration> call = mService.getConfiguration();

        Response<GetConfiguration> response = call.execute();

        System.out.println("response successful: " + response.isSuccessful());

        GetConfiguration getConfiguration = response.body();
        System.out.println(new Gson().toJson(getConfiguration));

        assertTrue(response.isSuccessful());
    }

    @Test
    public void getMovieDetail() throws Exception {
        System.out.println("getMovieDetail");

        Call<MovieDetail> call = mService.getMovieDetail(49026);

        Response<MovieDetail> response = call.execute();

        System.out.println("response successful: " + response.isSuccessful());

        MovieDetail movieDetail = response.body();
        System.out.println(new Gson().toJson(movieDetail));

        assertTrue(response.isSuccessful());
    }

    @Test
    public void getNowPlaying() throws Exception {
        System.out.println("getNowPlaying");

        Call<NowPlaying> call = mService.getNowPlaying(1);

        Response<NowPlaying> response = call.execute();

        System.out.println("response successful: " + response.isSuccessful());

        NowPlaying body = response.body();
        System.out.println(new Gson().toJson(body));

        assertTrue(response.isSuccessful());
    }
}



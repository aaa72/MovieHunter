package com.leo.moviehunter.tmdb.service;

import com.leo.moviehunter.tmdb.response.SearchMovie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDBService {

    @GET("locations/v1/cities/geoposition/search.json")
    Call<String> search(@Query("q") String q, @Query("apikey") String apikey);

    @GET("search/movie")
    Call<SearchMovie> searchMovie(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("language") String lang,
            @Query("page") int page,
            @Query("include_adult") boolean includeAdult,
            @Query("year") int year
    );

}

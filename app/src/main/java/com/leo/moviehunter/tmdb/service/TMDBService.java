package com.leo.moviehunter.tmdb.service;

import com.leo.moviehunter.tmdb.response.DiscoverMovie;
import com.leo.moviehunter.tmdb.response.SearchMovie;
import com.leo.moviehunter.tmdb.response.getGenres;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDBService {

    @GET("discover/movie")
    Call<DiscoverMovie> discoverMovie(@Query("api_key") String apiKey,
                                      @Query("language") String language,
                                      @Query("region") String region,
                                      @Query("sort_by") String sort_by,
                                      @Query("include_adult") boolean include_adult,
                                      @Query("page") int page,
                                      @Query("with_genres") String with_genres
    );

    @GET("genre/movie/list")
    Call<getGenres> getGenres(@Query("api_key") String apiKey,
                              @Query("language") String language
    );

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

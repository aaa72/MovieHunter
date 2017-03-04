package com.leo.moviehunter.tmdb.service;

import com.leo.moviehunter.tmdb.TMDBConstants;
import com.leo.moviehunter.tmdb.response.DiscoverMovie;
import com.leo.moviehunter.tmdb.response.GetConfiguration;
import com.leo.moviehunter.tmdb.response.GetGenres;
import com.leo.moviehunter.tmdb.response.MovieDetail;
import com.leo.moviehunter.tmdb.response.NowPlaying;
import com.leo.moviehunter.tmdb.response.SearchMovie;

import java.io.IOException;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class TMDBServiceManager {

    private static Retrofit mRetrofit;
    private static TMDBService mTMDBService;

    public synchronized static TMDBService getTMDBService() {
        lazyCreateTMDBService();
        return mTMDBService;
    }

    private synchronized static void lazyCreateRetrofit() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(TMDBConstants.TMDB_API_3_URL)
                    .client(new OkHttpClient.Builder().addInterceptor(new RequestInterceptor()).build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    private synchronized static void lazyCreateTMDBService() {
        lazyCreateRetrofit();
        if (mTMDBService == null) {
            mTMDBService = mRetrofit.create(TMDBService.class);
        }
    }

    private static class RequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();
            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter(TMDBConstants.TMDB_PARAM_KEY_API_KEY, TMDBConstants.TMDB_API_KEY)
                    .addQueryParameter(TMDBConstants.TMDB_PARAM_KEY_LANGUAGE, Locale.getDefault().getLanguage())
                    .addQueryParameter(TMDBConstants.TMDB_PARAM_KEY_REGION, Locale.getDefault().getCountry())
                    .build();
            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);
            return chain.proceed(requestBuilder.build());
        }
    }

    public interface TMDBService {

        @GET("discover/movie")
        Call<DiscoverMovie> discoverMovie(@Query("sort_by") String sort_by,
                                          @Query("include_adult") boolean include_adult,
                                          @Query("page") int page,
                                          @Query("with_genres") String with_genres
        );

        @GET("genre/movie/list")
        Call<GetGenres> getGenres();

        @GET("search/movie")
        Call<SearchMovie> searchMovie(
                @Query("query") String query,
                @Query("page") int page,
                @Query("include_adult") boolean includeAdult,
                @Query("year") int year
        );

        @GET("configuration")
        Call<GetConfiguration> getConfiguration();

        @GET("movie/{movie_id}")
        Call<MovieDetail> getMovieDetail(@Path("movie_id") int movieId);

        @GET("movie/now_playing")
        Call<NowPlaying> getNowPlaying(@Query("page")int page);
    }
}

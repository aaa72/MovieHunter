package com.leo.moviehunter.util;

import com.leo.moviehunter.data.Movie;
import com.leo.moviehunter.tmdb.response.MovieDetail;
import com.leo.moviehunter.tmdb.response.MovieResult;

public class TMDBUtil {

    public static Movie[] movieResults2Movies(MovieResult[] movieResult) {
        if (movieResult == null) {
            return null;
        }
        Movie[] movies = new Movie[movieResult.length];
        for (int i = 0; i < movieResult.length; i++) {
            movies[i] = TMDBUtil.movieResult2Movie(movieResult[i]);
        }
        return movies;
    }

    public static Movie movieResult2Movie(MovieResult movieResult) {
        if (movieResult == null) {
            return null;
        }
        Movie movie = new Movie();
        movie.setId(String.valueOf(movieResult.id));
        movie.setTitle(movieResult.title);
        movie.setOriginalTitle(movieResult.original_title);
        movie.setCoverImageUrl(movieResult.poster_path);
        movie.setOverview(movieResult.overview);
        movie.setGenreIds(CommonUtil.intArray2StringArray(movieResult.genre_ids));
        movie.setReleaseDate(movieResult.release_date);
        movie.setScore(movieResult.vote_average);
        movie.setOriginalLanguage(movieResult.original_language);
        return movie;
    }

    public static Movie movieDetail2Movie(MovieDetail movieDetail) {
        if (movieDetail == null) {
            return null;
        }
        Movie movie = new Movie();
        movie.setId(String.valueOf(movieDetail.id));
        movie.setTitle(movieDetail.title);
        movie.setOriginalTitle(movieDetail.original_title);
        movie.setCoverImageUrl(movieDetail.poster_path);
        movie.setOverview(movieDetail.overview);
        if (movieDetail.genres != null) {
            String genres[] = new String[movieDetail.genres.length];
            for (int i = 0; i < movieDetail.genres.length; i++) {
                genres[i] = String.valueOf(movieDetail.genres[i].id);
            }
            movie.setGenreIds(genres);
        }
        movie.setReleaseDate(movieDetail.release_date);
        movie.setScore(movieDetail.vote_average);
        movie.setOriginalLanguage(movieDetail.original_language);
        return movie;
    }
}

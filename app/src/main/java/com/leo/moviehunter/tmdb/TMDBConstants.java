package com.leo.moviehunter.tmdb;

public interface TMDBConstants {
    // URL
    String HTTPS_PROTOCOL = "https://";
    String TMDB_BASE_URL = HTTPS_PROTOCOL + "api.themoviedb.org/";
    String TMDB_API_VERSION = "3";
    String TMDB_API_3_URL = TMDB_BASE_URL + TMDB_API_VERSION + "/";

    // API KEY
    String TMDB_API_KEY = "59de81d980230bf9859e5e892a361866";

    // TMDB parameter key
    String TMDB_PARAM_KEY_API_KEY = "api_key";
    String TMDB_PARAM_KEY_LANGUAGE = "language";

    // Image size
    String IMAGE_SIZE = "w342";

    enum SortBy {
        popularity,
        release_date,
        revenue,
        primary_release_date,
        original_title,
        vote_average,
        vote_count,;

        public String asc() {
            return name() + ".asc";
        }

        public String desc() {
            return name() + ".desc";
        }
    }
}

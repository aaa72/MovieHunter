package com.leo.moviehunter.tmdb;

interface TMDBConstants {
    String HTTP_PROTOCAL = "http://";
    String HTTPS_PROTOCAL = "https://";
    String TMDB_BASE_URL = HTTPS_PROTOCAL + "api.themoviedb.org/";
    String TMDB_API_VERSION = "3";
    String TMDB_API_3_URL = TMDB_BASE_URL + TMDB_API_VERSION + "/";
    String TMDB_API_KEY = "59de81d980230bf9859e5e892a361866";

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

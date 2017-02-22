package com.leo.moviehunter.tmdb.response;

public class DiscoverMovie {
    public int page;
    public Result[] results;
    public int total_results;
    public int total_pages;

    public static class Result {
        public String poster_path;
        public boolean adult;
        public String overview;
        public String release_date;
        public int genre_ids[];
        public int id;
        public String original_title;
        public String original_language;
        public String title;
        public String backdrop_path;
        public float popularity;
        public int vote_count;
        public boolean video;
        public float vote_average;
    }
}

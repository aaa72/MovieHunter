package com.leo.moviehunter.tmdb.response;

public class SearchMovie {
    /**
     "page": 2,
     "results": [
     {
     "poster_path": "/fCsqP7OF208vdH4lfRKcYuQBVBi.jpg",
     "adult": false,
     "overview": "Based on a true story, a hot shot Washington DC lobbyist and his protégé go down hard as their schemes to peddle influence lead to corruption and murder.",
     "release_date": "2010-09-16",
     "genre_ids": [
     80,
     35,
     18
     ],
     "id": 45324,
     "original_title": "Casino Jack",
     "original_language": "en",
     "title": "Casino Jack",
     "backdrop_path": "/4lIfRQ3zNxtNvSuZBR7eW8zPG3T.jpg",
     "popularity": 1.995868,
     "vote_count": 51,
     "video": false,
     "vote_average": 5.8
     },
     ],
     "total_results": 803,
     "total_pages": 41
     */

    public int page;
    public Results[] results;
    public int total_results;
    public int total_pages;

    public static class Results {
        public String poster_path;
        public String adult;
        public String overview;
        public String release_date;
        public int[] genre_ids;
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

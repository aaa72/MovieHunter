package com.leo.moviehunter.data.user;

public class WatchItem {

    private String mMovieId;

    public String getMovieId() {
        return mMovieId;
    }

    public void setMovieId(String movieId) {
        mMovieId = movieId;
    }

    private long mAddedEpochTime;

    public long getAddedEpochTime() {
        return mAddedEpochTime;
    }

    public void setAddedEpochTime(long addedEpochTime) {
        mAddedEpochTime = addedEpochTime;
    }

    private String[] mGenreIds;

    public String[] getGenreIds() {
        return mGenreIds;
    }

    public void setGenreIds(String[] genreId) {
        mGenreIds = genreId;
    }
}

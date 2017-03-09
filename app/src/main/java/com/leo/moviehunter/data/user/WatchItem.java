package com.leo.moviehunter.data.user;

public class WatchItem {

    private int mMovieId;

    public int getMovieId() {
        return mMovieId;
    }

    public void setMovieId(int movieId) {
        mMovieId = movieId;
    }

    private long mAddedEpochTime;

    public long getAddedEpochTime() {
        return mAddedEpochTime;
    }

    public void setAddedEpochTime(long addedEpochTime) {
        mAddedEpochTime = addedEpochTime;
    }

    private int[] mGenreIds;

    public int[] getGenreIds() {
        return mGenreIds;
    }

    public void setGenreIds(int[] genreId) {
        mGenreIds = genreId;
    }
}

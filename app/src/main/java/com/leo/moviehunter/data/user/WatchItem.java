package com.leo.moviehunter.data.user;

public class WatchItem {

    private String mMovieId;

    public String getMovieId() {
        return mMovieId;
    }

    public void setMovieId(String movieId) {
        mMovieId = movieId;
    }

    private int mStatus;

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    private long mAddedEpochTime;

    public long getAddedEpochTime() {
        return mAddedEpochTime;
    }

    public void setAddedEpochTime(long addedEpochTime) {
        mAddedEpochTime = addedEpochTime;
    }

    private long mWatchedEpochTime;

    public long getWatchedEpochTime() {
        return mWatchedEpochTime;
    }

    public void setWatchedEpochTime(long watchedEpochTime) {
        mWatchedEpochTime = watchedEpochTime;
    }

    private String[] mGenreIds;

    public String[] getGenreIds() {
        return mGenreIds;
    }

    public void setGenreIds(String[] genreId) {
        mGenreIds = genreId;
    }

    private String mComment;

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    private float mScore;

    public float getScore() {
        return mScore;
    }

    public void setScore(float score) {
        mScore = score;
    }


    public interface Status {
        int TO_WATCH = 0x1;
        int WATCHED = 0x2;
    }

    public static boolean isToWatch(WatchItem watchItem) {
        return watchItem != null && (watchItem.getStatus() & Status.TO_WATCH) == Status.TO_WATCH;
    }

    public static boolean isWatched(WatchItem watchItem) {
        return watchItem != null && (watchItem.getStatus() & Status.WATCHED) == Status.WATCHED;
    }
}

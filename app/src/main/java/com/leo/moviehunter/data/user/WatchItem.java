package com.leo.moviehunter.data.user;

import android.os.Parcel;
import android.os.Parcelable;

public class WatchItem implements Parcelable {

    public WatchItem() {}

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieId);
        dest.writeInt(mStatus);
        dest.writeLong(mAddedEpochTime);
        dest.writeLong(mWatchedEpochTime);
        dest.writeStringArray(mGenreIds);
        dest.writeString(mComment);
        dest.writeFloat(mScore);
    }

    private WatchItem(Parcel in) {
        mMovieId = in.readString();
        mStatus = in.readInt();
        mAddedEpochTime = in.readLong();
        mWatchedEpochTime = in.readLong();
        mGenreIds = in.createStringArray();
        mComment = in.readString();
        mScore = in.readFloat();
    }

    public static final Parcelable.Creator<WatchItem> CREATOR = new Parcelable.Creator<WatchItem>() {
        public WatchItem createFromParcel(Parcel in) {
            return new WatchItem(in);
        }

        public WatchItem[] newArray(int size) {
            return new WatchItem[size];
        }
    };

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

package com.leo.moviehunter.provider;

import android.content.ContentResolver;
import android.net.Uri;

public class UserDataStore {

    public static final String AUTHORITY = "com.leo.moviehunter.userdata";

    // URI
    public static Uri URI_WATCH_LIST = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTHORITY + "/" + TableWatchList.TableName);
    public static Uri URI_MOVIE_GENRE = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTHORITY + "/" + TableMovieGenre.TableName);


    // TABLE
    public interface TableWatchList {
        String TableName = "watch_list";

        // columns
        String MovieId = "movie_id";
        String AddedEpochTime = "added_epoch_time";
    }

    public interface TableMovieGenre {
        String TableName = "movie_genre";

        // columns
        String MovieId = "movie_id";
        String GenreId = "genre_id";
    }
}

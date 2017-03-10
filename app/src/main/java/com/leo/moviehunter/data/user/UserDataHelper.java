package com.leo.moviehunter.data.user;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.leo.moviehunter.provider.UserDataStore;
import com.leo.moviehunter.provider.UserDataStore.TableMovieGenre;
import com.leo.moviehunter.provider.UserDataStore.TableWatchList;
import com.leo.moviehunter.util.CommonUtil;
import com.leo.moviehunter.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UserDataHelper {
    private static final String TAG = "UserDataHelper";

    public static List<WatchItem> getWatchList(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(UserDataStore.URI_WATCH_LIST, null, null, null, null);
            if (cursor != null) {
                ArrayList<WatchItem> list = new ArrayList<>();
                final int idxMovieId = cursor.getColumnIndexOrThrow(TableWatchList.MovieId);
                final int idxAddedEpochTime = cursor.getColumnIndexOrThrow(TableWatchList.AddedEpochTime);
                for (cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {
                    WatchItem item = new WatchItem();
                    item.setMovieId(cursor.getInt(idxMovieId));
                    item.setAddedEpochTime(cursor.getLong(idxAddedEpochTime));
                    item.setGenreIds(getGenreIds(context, cursor.getInt(idxMovieId)));
                    list.add(item);
                }
                return list;
            }
        } catch (Exception e) {
            Log.w(TAG, "" + e, e);
        } finally {
            CommonUtil.closeCursor(cursor);
        }
        return null;
    }

    public static int[] getGenreIds(Context context, int movieId) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(UserDataStore.URI_MOVIE_GENRE, null, TableMovieGenre.MovieId + "=?", new String[] {String.valueOf(movieId)}, null);
            if (cursor != null) {
                final int idxGenreId = cursor.getColumnIndexOrThrow(TableMovieGenre.GenreId);
                final int[] genreIds = new int[cursor.getCount()];
                int i;
                for (i = 0, cursor.moveToFirst() ; !cursor.isAfterLast() ; i++, cursor.moveToNext()) {
                    genreIds[i] = cursor.getInt(idxGenreId);
                }
                return genreIds;
            }
        } catch (Exception e) {
            Log.w(TAG, "" + e, e);
        } finally {
            CommonUtil.closeCursor(cursor);
        }
        return null;
    }

    public static int addToWatchList(Context context, List<WatchItem> list) {
        if (list == null) {
            return -1;
        }

        if (list.size() == 0) {
            return 0;
        }

        ContentValues[] values = new ContentValues[list.size()];
        for (int i = 0; i < list.size(); i++) {
            WatchItem item = list.get(i);
            values[i] = new ContentValues();
            values[i].put(TableWatchList.MovieId, item.getMovieId());
            values[i].put(TableWatchList.AddedEpochTime, item.getMovieId());
            addToMovieGenre(context, item.getMovieId(), item.getGenreIds());
        }
        try {
            return context.getContentResolver().bulkInsert(UserDataStore.URI_WATCH_LIST, values);
        } catch (Exception e) {
            Log.w(TAG, "" + e, e);
        }

        return -1;
    }

    public static void addToMovieGenre(Context context, int movieId, int[] genreIds) {
        if (genreIds == null || genreIds.length <= 0) {
            return;
        }

        ContentValues[] values = new ContentValues[genreIds.length];
        for (int i = 0; i < genreIds.length; i++) {
            values[i] = new ContentValues();
            values[i].put(TableMovieGenre.MovieId, movieId);
            values[i].put(TableMovieGenre.GenreId, genreIds[i]);
        }
        try {
            context.getContentResolver().bulkInsert(UserDataStore.URI_MOVIE_GENRE, values);
        } catch (Exception e) {
            Log.w(TAG, "" + e, e);
        }
    }

    public static int deleteFromWatchList(Context context, List<WatchItem> list) {
        if (list == null || list.size() <= 0) {
            return 0;
        }

        int count = 0;
        for (WatchItem watchItem : list) {
            int ret = context.getContentResolver().delete(UserDataStore.URI_WATCH_LIST,
                    TableWatchList.MovieId + "=" + watchItem.getMovieId(), null);
            if (ret > 0) {
                count++;
                deleteFromMovieGenre(context, watchItem.getMovieId());
            }
        }
        return count;
    }

    public static int deleteFromMovieGenre(Context context, int movieId) {
        return context.getContentResolver().delete(UserDataStore.URI_MOVIE_GENRE,
                TableMovieGenre.MovieId + "=" + movieId, null);
    }
}

package com.leo.moviehunter.data.user;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.leo.moviehunter.data.user.WatchItem.Status;
import com.leo.moviehunter.provider.UserDataStore;
import com.leo.moviehunter.provider.UserDataStore.TableMovieGenre;
import com.leo.moviehunter.provider.UserDataStore.TableWatchList;
import com.leo.moviehunter.util.CommonUtil;
import com.leo.moviehunter.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UserDataHelper {
    private static final String TAG = "UserDataHelper";

    public static List<WatchItem> getToWatchList(Context context) {
        final String where = TableWatchList.Status + " & ? != 0";
        final String[] whereArgs = new String[] {String.valueOf(Status.TO_WATCH)};
        return _getWatchList(context, where, whereArgs, null);
    }

    public static List<WatchItem> getWatchedList(Context context) {
        final String where = TableWatchList.Status + " & ? != 0";
        final String[] whereArgs = new String[] {String.valueOf(Status.WATCHED)};
        return _getWatchList(context, where, whereArgs, null);
    }

    private static List<WatchItem> _getWatchList(Context context, String where, String[] whereArgs, String sortBy) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(UserDataStore.URI_WATCH_LIST, null, where, whereArgs, sortBy);
            if (cursor != null) {
                ArrayList<WatchItem> list = new ArrayList<>();
                final int idxMovieId = cursor.getColumnIndexOrThrow(TableWatchList.MovieId);
                final int idxStatus = cursor.getColumnIndexOrThrow(TableWatchList.Status);
                final int idxAddedEpochTime = cursor.getColumnIndexOrThrow(TableWatchList.AddedEpochTime);
                final int idxWatchedEpochTime = cursor.getColumnIndexOrThrow(TableWatchList.WatchedEpochTime);
                final int idxComment = cursor.getColumnIndexOrThrow(TableWatchList.Comment);
                final int idxScore = cursor.getColumnIndexOrThrow(TableWatchList.Score);
                for (cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {
                    WatchItem item = new WatchItem();
                    item.setMovieId(cursor.getString(idxMovieId));
                    item.setStatus(cursor.getInt(idxStatus));
                    item.setAddedEpochTime(cursor.getLong(idxAddedEpochTime));
                    item.setWatchedEpochTime(cursor.getLong(idxWatchedEpochTime));
                    item.setComment(cursor.getString(idxComment));
                    item.setScore(cursor.getFloat(idxScore));
                    item.setGenreIds(getGenreIds(context, cursor.getString(idxMovieId)));
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

    public static String[] getGenreIds(Context context, String movieId) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(UserDataStore.URI_MOVIE_GENRE, null, TableMovieGenre.MovieId + "=?", new String[] {String.valueOf(movieId)}, null);
            if (cursor != null) {
                final int idxGenreId = cursor.getColumnIndexOrThrow(TableMovieGenre.GenreId);
                final String[] genreIds = new String[cursor.getCount()];
                int i;
                for (i = 0, cursor.moveToFirst() ; !cursor.isAfterLast() ; i++, cursor.moveToNext()) {
                    genreIds[i] = cursor.getString(idxGenreId);
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
        for (WatchItem watchItem : list) {
            watchItem.setStatus(watchItem.getStatus() | Status.TO_WATCH);
            watchItem.setAddedEpochTime(System.currentTimeMillis());
        }
        return _addToWatchList(context, list);
    }

    public static int addToWatchedList(Context context, List<WatchItem> list) {
        for (WatchItem watchItem : list) {
            watchItem.setStatus(watchItem.getStatus() & ~Status.TO_WATCH | Status.WATCHED);
        }
        return _addToWatchList(context, list);
    }

    private static int _addToWatchList(Context context, List<WatchItem> list) {
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
            values[i].put(TableWatchList.Status, item.getStatus());
            values[i].put(TableWatchList.AddedEpochTime, item.getAddedEpochTime());
            values[i].put(TableWatchList.WatchedEpochTime, item.getWatchedEpochTime());
            values[i].put(TableWatchList.Comment, item.getComment());
            values[i].put(TableWatchList.Score, item.getScore());
            addToMovieGenre(context, item.getMovieId(), item.getGenreIds());
        }
        try {
            return context.getContentResolver().bulkInsert(UserDataStore.URI_WATCH_LIST, values);
        } catch (Exception e) {
            Log.w(TAG, "" + e, e);
        }

        return -1;
    }

    public static void addToMovieGenre(Context context, String movieId, String[] genreIds) {
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

    public static int deleteFromToWatchList(Context context, List<WatchItem> list) {
        for (WatchItem watchItem : list) {
            watchItem.setStatus(watchItem.getStatus() ^ Status.TO_WATCH);
            watchItem.setAddedEpochTime(0);
        }
        return _deleteFromWatchList(context, list);
    }

    public static int deleteFromWatchedList(Context context, List<WatchItem> list) {
        for (WatchItem watchItem : list) {
            watchItem.setStatus(watchItem.getStatus() ^ Status.WATCHED);
            watchItem.setWatchedEpochTime(0);
        }
        return _deleteFromWatchList(context, list);
    }

    private static int _deleteFromWatchList(Context context, List<WatchItem> list) {
        if (list == null || list.size() <= 0) {
            return 0;
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ArrayList<ContentProviderOperation> deleteGenreOps = new ArrayList<>();
        for (WatchItem watchItem : list) {
            final String where = TableWatchList.MovieId + "=?";
            final String[] whereArgs = new String[] {watchItem.getMovieId()};
            if (watchItem.getStatus() > 0) {
                ops.add(ContentProviderOperation.newUpdate(UserDataStore.URI_WATCH_LIST)
                        .withValue(TableWatchList.Status, watchItem.getStatus())
                        .withSelection(where, whereArgs)
                        .build());
            } else {
                ops.add(ContentProviderOperation.newDelete(UserDataStore.URI_WATCH_LIST)
                        .withSelection(where, whereArgs)
                        .build());
                deleteGenreOps.add(ContentProviderOperation.newDelete(UserDataStore.URI_MOVIE_GENRE)
                        .withSelection(TableMovieGenre.MovieId + "=?", new String[] {watchItem.getMovieId()})
                        .build());
            }
        }

        int count = 0;
        try {
            ContentProviderResult[] results = context.getContentResolver().applyBatch(UserDataStore.AUTHORITY, ops);
            for (int i = 0; i < results.length; i++) {
                ContentProviderResult result = results[i];
                if (result.count != null && result.count.intValue() > 0) {
                    count++;
                }
            }
            context.getContentResolver().applyBatch(UserDataStore.AUTHORITY, deleteGenreOps);
        } catch (Exception e) {
            Log.w(TAG, "" + e, e);
        }

        return count;
    }

    public static int deleteFromMovieGenre(Context context, String movieId) {
        return context.getContentResolver().delete(UserDataStore.URI_MOVIE_GENRE,
                TableMovieGenre.MovieId + "=?", new String[] {movieId});
    }
}

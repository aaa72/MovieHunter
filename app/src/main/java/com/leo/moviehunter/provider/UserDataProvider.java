package com.leo.moviehunter.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.leo.moviehunter.provider.UserDataStore.TableMovieGenre;
import com.leo.moviehunter.provider.UserDataStore.TableWatchList;
import com.leo.moviehunter.util.Log;

import static com.leo.moviehunter.provider.UserDataStore.AUTHORITY;

public class UserDataProvider extends ContentProvider {
    private static final String TAG = "UserDataProvider";

    private DatabaseHelper mDBHelper;

    // Matcher
    private enum Matcher {
        WATCH_LIST,
        MOVIE_GENRE,
    }
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, TableWatchList.TableName, Matcher.WATCH_LIST.ordinal());
        sUriMatcher.addURI(AUTHORITY, TableMovieGenre.TableName, Matcher.MOVIE_GENRE.ordinal());
    }

    // VND
    private static final String VND_SUBTYPE = "vnd." + UserDataStore.AUTHORITY;
    private static final String VND_DIR = "vnd.android.cursor.dir/" + VND_SUBTYPE;
    private static final String VND_ITEM = "vnd.android.cursor.item/" + VND_SUBTYPE;

    @Override
    public boolean onCreate() {
        mDBHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        if (match < 0) {
            return null;
        }

        String vnd = null;

        switch (Matcher.values()[match]) {
            case WATCH_LIST:
                vnd = VND_ITEM;
                break;
        }

        return vnd;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int match = sUriMatcher.match(uri);
        if (match < 0) {
            return null;
        }

        SQLiteDatabase db;
        try {
            db = mDBHelper.getReadableDatabase();
        } catch (Exception e) {
            Log.w(TAG, "query fail", e);
            return null;
        }

        Cursor cursor = null;

        switch (Matcher.values()[match]) {
            case WATCH_LIST: {
                cursor = db.query(TableWatchList.TableName
                        , projection, selection, selectionArgs, null, null, sortOrder);
            }
            break;

            case MOVIE_GENRE: {
                cursor = db.query(TableMovieGenre.TableName
                        , projection, selection, selectionArgs, null, null, sortOrder);
            }
            break;

            default: {
                Log.w(TAG, "Unknown uri: " + uri);
            }
        }

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        if (match < 0) {
            return null;
        }

        SQLiteDatabase db;
        try {
            db = mDBHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.w(TAG, "insert fail", e);
            return null;
        }

        Uri retUri = null;

        switch (Matcher.values()[match]) {
            case WATCH_LIST: {
                long id = db.insert(TableWatchList.TableName, null, values);
                if (id > 0) {
                    retUri = ContentUris.withAppendedId(uri, id);
                }
            }
            break;

            case MOVIE_GENRE: {
                long id = db.insert(TableMovieGenre.TableName, null, values);
                if (id > 0) {
                    retUri = ContentUris.withAppendedId(uri, id);
                }
            }
            break;

            default: {
                Log.w(TAG, "Unknown uri: " + uri);
            }
        }

        if (retUri != null) {
            notifyChange(retUri);
        }

        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        if (match < 0) {
            return 0;
        }

        SQLiteDatabase db;
        try {
            db = mDBHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.w(TAG, "delete fail", e);
            return 0;
        }

        int count = 0;

        switch (Matcher.values()[match]) {
            case WATCH_LIST: {
                count = db.delete(TableWatchList.TableName, selection, selectionArgs);
            }
            break;

            case MOVIE_GENRE: {
                count = db.delete(TableMovieGenre.TableName, selection, selectionArgs);
            }
            break;

            default: {
                Log.w(TAG, "Unknown uri: " + uri);
            }
        }

        if (count > 0) {
            notifyChange(uri);
        }

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        if (match < 0) {
            return 0;
        }

        SQLiteDatabase db;
        try {
            db = mDBHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.w(TAG, "update fail", e);
            return 0;
        }

        int count = 0;

        switch (Matcher.values()[match]) {
            case WATCH_LIST: {
                count = db.update(TableWatchList.TableName, values, selection, selectionArgs);
            }
            break;

            case MOVIE_GENRE: {
                count = db.update(TableMovieGenre.TableName, values, selection, selectionArgs);
            }
            break;
        }

        if (count > 0) {
            notifyChange(uri);
        }
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final int match = sUriMatcher.match(uri);
        if (match < 0) {
            return 0;
        }

        if (values == null) {
            return 0;
        }

        SQLiteDatabase db;
        try {
            db = mDBHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.w(TAG, "bulkInsert fail", e);
            return 0;
        }

        int count = 0;

        switch (Matcher.values()[match]) {
            case WATCH_LIST: {
                db.beginTransaction();
                try {
                    Uri uris[] = new Uri[values.length];
                    for (int i = 0; i < values.length; i++) {
                        long id = db.insertWithOnConflict(TableWatchList.TableName, null, values[i], SQLiteDatabase.CONFLICT_REPLACE);
                        if (id > 0) {
                            count++;
                            uris[i] = ContentUris.withAppendedId(uri, id);
                        } else {
                            uris[i] = null;
                        }
                    }
                    db.setTransactionSuccessful();
                    notifyChanges(uris);
                } finally {
                    db.endTransaction();
                }
            }
            break;

            case MOVIE_GENRE: {
                db.beginTransaction();
                try {
                    Uri uris[] = new Uri[values.length];
                    for (int i = 0; i < values.length; i++) {
                        long id = db.insertWithOnConflict(TableMovieGenre.TableName, null, values[i], SQLiteDatabase.CONFLICT_IGNORE);
                        if (id > 0) {
                            count++;
                            uris[i] = ContentUris.withAppendedId(uri, id);
                        } else {
                            uris[i] = null;
                        }
                    }
                    db.setTransactionSuccessful();
                    notifyChanges(uris);
                } finally {
                    db.endTransaction();
                }
            }
            break;
        }

        return count;
    }

    private void notifyChanges(Uri[] uris) {
        for (Uri uri : uris) {
            notifyChange(uri);
        }
    }

    private void notifyChange(Uri uri) {
        if (uri == null) {
            return;
        }
        getContext().getContentResolver().notifyChange(uri, null);
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "userdata.db";
        private static final int DB_VERSION = 1;

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_WATCH_LIST);
            db.execSQL(SQL_CREATE_MOVIE_GENRE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        private final String SQL_CREATE_WATCH_LIST = "create table " + TableWatchList.TableName + "( "
                + "_id integer primary key"
                + ", "
                + TableWatchList.MovieId + " text unique not null"
                + ", "
                + TableWatchList.Status + " integer"
                + ", "
                + TableWatchList.AddedEpochTime + " integer"
                + ", "
                + TableWatchList.WatchedEpochTime + " integer"
                + ", "
                + TableWatchList.Comment + " text"
                + ", "
                + TableWatchList.Score + " real"
                + ");";

        private final String SQL_CREATE_MOVIE_GENRE = "create table " + TableMovieGenre.TableName + "( "
                + "_id integer primary key"
                + ", "
                + TableMovieGenre.MovieId + " text unique not null"
                + ", "
                + TableMovieGenre.GenreId + " text"
                + ");";
    }
}

package com.leo.moviehunter.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MyCollectionProvider extends ContentProvider {
    private static final String TAG = "MyCollectionProvider";

    public static final String AUTHORITY = "com.leo.moviehunter.my_collection";
    public static final Uri URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri URI_DATA = Uri.parse("content://" + AUTHORITY + "/" + TableData.TableName);

    private MyCollectionDatabaseHelper mDBHelper;

    private interface Matcher {
        int Data = 1;
    }

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, TableData.TableName, Matcher.Data);
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new MyCollectionDatabaseHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (sUriMatcher.match(uri)) {
            case Matcher.Data: {
                return mDBHelper.getReadableDatabase().query(TableData.TableName
                        , projection, selection, selectionArgs, null, null, sortOrder);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case Matcher.Data: {
                long id = mDBHelper.getWritableDatabase().insert(TableData.TableName
                        , null, values);
                if (id > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return Uri.parse(uri + "/" + id);
                }
                return null;
            }
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (ContentValues cv : values) {
                db.insert(TableData.TableName, null, cv);
            }
            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
        } finally {
            db.endTransaction();
        }
        return values.length;
    }

    public interface TableData {
        String TableName = "data";
        String MovieId = "movie_id";
    }

    private class MyCollectionDatabaseHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "my_collection";
        private static final int DB_VERSION = 1;

        public MyCollectionDatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        private final String SQL_CREATE = "create table " + TableData.TableName + "( "
                + TableData.MovieId + " text primary key"
                + ");";
    }
}

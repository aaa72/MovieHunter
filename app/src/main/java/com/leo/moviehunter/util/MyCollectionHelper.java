package com.leo.moviehunter.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.leo.moviehunter.data.CollectionItem;
import com.leo.moviehunter.provider.MyCollectionProvider;

import java.util.ArrayList;
import java.util.List;

public class MyCollectionHelper {
    private static final String TAG = "MyCollectionHelper";

    public static List<CollectionItem> getMyCollection(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MyCollectionProvider.URI_DATA, null, null, null, null);
            if (cursor != null) {
                ArrayList<CollectionItem> list = new ArrayList<>();
                int idxMovieId = cursor.getColumnIndexOrThrow(MyCollectionProvider.TableData.MovieId);
                for (cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {
                    CollectionItem item = new CollectionItem();
                    item.setMovieId(cursor.getInt(idxMovieId));
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

    public static void addMyCollection(Context context, List<CollectionItem> list) {
        if (list == null || list.size() <= 0) {
            return;
        }

        ContentValues[] values = new ContentValues[list.size()];
        for (int i = 0; i < list.size(); i++) {
            CollectionItem item = list.get(i);
            values[i] = new ContentValues();
            values[i].put(MyCollectionProvider.TableData.MovieId, item.getMovieId());
        }
        try {
            context.getContentResolver().bulkInsert(MyCollectionProvider.URI_DATA, values);
        } catch (Exception e) {
            Log.w(TAG, "" + e, e);
        }
    }
}

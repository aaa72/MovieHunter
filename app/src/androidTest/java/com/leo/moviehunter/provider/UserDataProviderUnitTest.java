package com.leo.moviehunter.provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import com.leo.moviehunter.data.user.WatchItem;
import com.leo.moviehunter.datahelper.UserDataHelperFactory;
import com.leo.moviehunter.provider.UserDataStore.TableWatchList;
import com.leo.moviehunter.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class UserDataProviderUnitTest extends ProviderTestCase2<UserDataProvider> {
    private static final String TAG = "UserDataProviderUnitTest";

    public UserDataProviderUnitTest() {
        super(UserDataProvider.class, UserDataStore.AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        super.setUp();
    }

    @Test
    public void testGetWatchList() throws Exception {
        WatchItem item;
        item = new WatchItem();
        ArrayList<WatchItem> list = new ArrayList<>();
        item.setMovieId("1");
        item.setGenreIds(Arrays.asList(new String[] {"1","2","3"}));
        list.add(item);

        item = new WatchItem();
        item.setMovieId("2");
        item.setGenreIds(Arrays.asList(new String[] {"1","2"}));
        list.add(item);

        item = new WatchItem();
        item.setMovieId("3");
        item.setGenreIds(Arrays.asList(new String[] {"2","3"}));
        list.add(item);

        int ret = UserDataHelperFactory.get(getMockContext()).addToWatchList(list);
        assertTrue(ret > 0);

        // Get
        List<WatchItem> list2 = UserDataHelperFactory.get(getMockContext()).getToWatchList();
        assertTrue(list2 != null && list2.size() == ret);

        // delete
        int delRet = UserDataHelperFactory.get(getMockContext()).deleteFromToWatchList(list.subList(0, 1));
        assertTrue(delRet == 1);
    }

    @Test
    public void testInsertWatchList() throws Exception {
        final String movieId = "333";

        // insert normal item
        Uri uri = insertTestItem(movieId);
        Log.d(TAG, "testInsertWatchList() - uri: " + uri);
        final long id = ContentUris.parseId(uri);
        Log.d(TAG, "testInsertWatchList() - movieId: " + movieId + ", uri id = " + id);
        assertTrue(id > 0);

        // insert duplication item
        Uri uri2 = insertTestItem(movieId);
        Log.d(TAG, "testInsertWatchList() - uri1 = " + uri + ", uri2 = " + uri2);
        assertEquals(uri2, null);
    }

    @Test
    public void testUpdateWatchList() throws Exception {
        final String movieId = "111";
        insertTestItem(movieId);

        // update normal item
        int count = updateItem(movieId);
        Log.d(TAG, "testUpdateWatchList() - count: " + count);
        assertTrue(count > 0);

        // update non exist item
        count = updateItem("222");
        assertEquals(count, 0);
    }

    @Test
    public void testDeleteWatchList() throws Exception {
        final String movieId = "111";
        insertTestItem(movieId);

        // delete normal item
        int count = deleteItem(movieId);
        Log.d(TAG, "testDeleteWatchList() - count: " + count);
        assertTrue(count > 0);

        // delete non exist item
        count = deleteItem(movieId);
        assertEquals(count, 0);
    }

    @Test
    public void testQueryWatchList() throws Exception {
        insertTestItem("111");
        insertTestItem("222");

        // query all
        Cursor cursor = getMockContentResolver().query(UserDataStore.URI_WATCH_LIST, null, null, null, null);
        assertTrue(cursor != null && cursor.getCount() == 2);
        cursor.close();

        // query single
        final String where = TableWatchList.MovieId + "=?";
        final String[] whereArgs = new String[] {String.valueOf(111)};
        cursor = getMockContentResolver().query(UserDataStore.URI_WATCH_LIST, null, where, whereArgs, null);
        assertTrue(cursor != null && cursor.getCount() == 1);
    }

    private Uri insertTestItem(String movieId) {
        ContentValues values = new ContentValues();
        values.put(TableWatchList.MovieId, movieId);
        values.put(TableWatchList.AddedEpochTime, System.currentTimeMillis());
        Uri uri = getMockContentResolver().insert(UserDataStore.URI_WATCH_LIST, values);
        return uri;
    }

    private int updateItem(String movieId) {
        ContentValues values = new ContentValues();
        values.put(TableWatchList.AddedEpochTime, System.currentTimeMillis());
        String where = TableWatchList.MovieId + "=?";
        String[] whereArgs = new String[] {movieId};
        int count = getMockContentResolver().update(UserDataStore.URI_WATCH_LIST, values, where, whereArgs);
        return count;
    }

    private int deleteItem(String movieId) {
        String where = TableWatchList.MovieId + "=?";
        String[] whereArgs = new String[] {movieId};
        int count = getMockContentResolver().delete(UserDataStore.URI_WATCH_LIST, where, whereArgs);
        return count;
    }
}
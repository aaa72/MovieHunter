package com.leo.moviehunter.provider;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.leo.moviehunter.data.user.WatchItem;
import com.leo.moviehunter.datahelper.UserDataHelperFactory;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FirebaseUserDataProviderUnitTest {
    private static final String TAG = "FirebaseUserDataProviderUnitTest";

    @Test
    public void testFirebaseDatabase() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.leo.moviehunter", appContext.getPackageName());

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

        int ret = UserDataHelperFactory.get(appContext).addToWatchedList(list);
        assertTrue(ret > 0);

        // Get
        List<WatchItem> list2 = UserDataHelperFactory.get(appContext).getWatchedList();
        assertTrue(list2 != null && list2.size() == ret);

        // delete
        int delRet = UserDataHelperFactory.get(appContext).deleteFromToWatchList(list.subList(0, 1));
        assertTrue(delRet == 1);
    }
}
package com.leo.moviehunter.util;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.leo.moviehunter.data.Movie;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CacheManagerInstrumentedTest {
    private static final String TAG = "CacheManagerInstrumentedTest";

    @Test
    public void testCache() throws Exception {
        CacheManager cacheManager = CacheManager.getInstance(InstrumentationRegistry.getTargetContext());
        DualCache<Movie> cache = cacheManager.getCache(Movie.class);

        final String id = "111";
        Movie movie = new Movie();
        movie.setId(id);
        movie.setTitle("Test");
        cache.put(movie.getId(), movie);

        // load from memory
        movie = cache.get(id);
        assertEquals(movie.getId(), id);

        // load from disk
        cache.invalidateRAM();
        movie = cache.get(id);
        assertEquals(movie.getId(), id);

        // remove
        cache.invalidateRAM();
        cache.invalidateDisk();
        movie = cache.get(id);
        assertEquals(movie, null);
    }
}

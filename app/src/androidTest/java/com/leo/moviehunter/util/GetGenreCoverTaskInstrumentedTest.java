package com.leo.moviehunter.util;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.leo.moviehunter.fragment.GenreMainFragment;
import com.leo.moviehunter.rule.FragmentTestRule;
import com.leo.moviehunter.task.GetGenreCoverUrlTask;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GetGenreCoverTaskInstrumentedTest {
    private static final String TAG = "GetGenreCoverTask";

    private GetGenreCoverUrlTask mTask;

    @Rule
    public FragmentTestRule<GenreMainFragment> mFragmentRule = new FragmentTestRule<>(GenreMainFragment.class);

    private IdlingResource mIdlingResource;

    @Before
    public void before() {
//        mIdlingResource = mFragmentRule.getFragment().getIdlingResouce();
//        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void testGetGenreCoverUrlTask() throws Exception {
        // TODO
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}

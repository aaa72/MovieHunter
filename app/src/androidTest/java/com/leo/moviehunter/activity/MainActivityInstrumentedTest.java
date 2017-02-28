package com.leo.moviehunter.activity;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.leo.moviehunter.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testButtonClick() throws Exception {

        // launch activity
//        intended(hasComponent(new ComponentName(getTargetContext(), GenreActivity.class)));

        // input text
//        onView(withId(R.id.edit)).perform(typeText("12345"), closeSoftKeyboard());

        // click button
//        onView(withId(R.id.button)).perform(click());

        // check text contain text
        onView(withId(R.id.text)).check(matches(withText("12345")));
    }

//    @Rule
//    public IntentsTestRule<MainActivity> mIntentRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void testRecyclerView() {
        // find view in list/grid view and click
//        onData(allOf(is(instanceOf(String.class)), is("驚悚"))).perform(click());

        // find view in recycler view and click
        onView(withId(R.id.recycler)).perform(RecyclerViewActions.actionOnItem(withText("惊悚"), click()));

        // check launch activity
//        intended(hasComponent(new ComponentName(getTargetContext(), GenreActivity.class)));
//        intended(hasComponent(GenreActivity.class.getName()));
    }
}

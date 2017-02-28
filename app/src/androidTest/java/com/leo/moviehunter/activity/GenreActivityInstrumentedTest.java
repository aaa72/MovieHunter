package com.leo.moviehunter.activity;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.leo.moviehunter.R;
import com.leo.moviehunter.util.MHConstants;
import com.leo.moviehunter.util.RecyclerViewItemCountAssertion;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GenreActivityInstrumentedTest {

//    @Rule
//    public ActivityTestRule<GenreActivity> mActivityRule = new ActivityTestRule<>(GenreActivity.class);

    @Rule
    public ActivityTestRule<GenreActivity> mActivityRule =
            new ActivityTestRule<GenreActivity>(GenreActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, MainActivity.class);
                    result.putExtra(MHConstants.BUNDLE_KEY_GENRE_ID, 53);
                    return result;
                }
            };

    @Test
    public void testActivityInput() {
//        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        Intent intent = new Intent(targetContext, GenreActivity.class);
//        intent.putExtra(MHConstants.BUNDLE_KEY_GENRE_ID, 53);
//        mActivityRule.launchActivity(intent);

        // check recycler view item count
//        onView(withId(R.id.recycler)).check(new RecyclerViewItemCountAssertion(greaterThan(0)));

        onView(withId(R.id.recycler)).check(matches(withText(containsString("世界"))));
    }
}

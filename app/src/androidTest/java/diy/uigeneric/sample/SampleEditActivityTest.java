package diy.uigeneric.sample;

import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;

import diy.uigeneric.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class SampleEditActivityTest {

    private static final String TAG = "SampleEditActivityTest";

    @Rule
    public ActivityTestRule<SampleEditActivity> activityTestRule = new ActivityTestRule<>(SampleEditActivity.class);

    @Test
    public void test() {
        Log.d(TAG, "test");

        onView(withId(R.id.edit_name)).perform(typeText("Hello")).check(matches(withText("Hello")));
    }

}

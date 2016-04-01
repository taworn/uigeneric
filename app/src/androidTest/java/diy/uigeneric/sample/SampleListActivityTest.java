package diy.uigeneric.sample;

import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;

public class SampleListActivityTest {

    private static final String TAG = "SampleListActivityTest";

    @Rule
    public ActivityTestRule<SampleListActivity> activityTestRule = new ActivityTestRule<>(SampleListActivity.class);

    @Test
    public void test() {
        Log.d(TAG, "test");
    }

}

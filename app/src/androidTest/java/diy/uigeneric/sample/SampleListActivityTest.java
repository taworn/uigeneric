package diy.uigeneric.sample;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SampleListActivityTest {

    private static final String TAG = SampleListActivityTest.class.getSimpleName();

    @Rule
    public ActivityTestRule<SampleListActivity> activityTestRule = new ActivityTestRule<>(SampleListActivity.class);

    @Test
    public void test() {
        Log.d(TAG, "test");
    }

}

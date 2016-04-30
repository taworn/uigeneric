package diy.uigeneric.sample;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import diy.uigeneric.R;
import diy.uigeneric.data.Sample;
import diy.uigeneric.data.SampleDataSource;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SampleListActivityTest {

    private static final String TAG = SampleListActivityTest.class.getSimpleName();

    @Rule
    public ActivityTestRule<SampleListActivity> activityTestRule = new ActivityTestRule<>(SampleListActivity.class);

    @Before
    public void before() {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleDataSource source = new SampleDataSource(context);
        source.open();
        source.removeAll();

        Sample sample;
        sample = new Sample();
        sample.setName("a");
        source.insert(sample);
        sample = new Sample();
        sample.setName("bb");
        source.insert(sample);
        sample = new Sample();
        sample.setName("ccc");
        source.insert(sample);

        source.close();
    }

    @Test
    public void test() {
        Context context = InstrumentationRegistry.getTargetContext();

        // opens menu and clicks Remove All
        openActionBarOverflowOrOptionsMenu(context);
        onView(withText("Remove All")).perform(click());

        // confirms dialog
        onView(withText(R.string.sample_remove_all_dialog_positive)).perform(click());

        // sees result
        SampleDataSource source = new SampleDataSource(context);
        source.open();
        assertTrue(source.count() == 0);
        source.close();
    }

}

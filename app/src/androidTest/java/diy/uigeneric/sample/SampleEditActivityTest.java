package diy.uigeneric.sample;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import diy.uigeneric.R;
import diy.uigeneric.data.Sample;
import diy.uigeneric.data.SampleDataSource;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SampleEditActivityTest {

    private static final String TAG = "SampleEditActivityTest";

    public ActivityTestRule<SampleEditActivity> activityTestRule = new ActivityTestRule<>(SampleEditActivity.class, false, false);

    @Test
    public void testAdd() {
        SampleDataSource source = new SampleDataSource(InstrumentationRegistry.getTargetContext());
        source.open();
        source.deleteAll();

        activityTestRule.launchActivity(null);
        onView(withId(R.id.edit_name)).perform(typeText("Hello"));
        onView(withId(R.id.action_save)).perform(click());

        long id = activityTestRule.getActivity().getItemId();
        Sample item = source.get(id);
        assertTrue(item.getName().equals("Hello"));
        List<Sample> list = source.list(false, null, "H", null);
        assertTrue(list.size() == 1);
        assertTrue(list.get(0).getName().equals("Hello"));

        Log.d(TAG, "ok");
        source.close();
    }

    @Test
    public void testEdit() {
        SampleDataSource source = new SampleDataSource(InstrumentationRegistry.getTargetContext());
        source.open();
        source.deleteAll();

        // pre-adds record
        Sample item = new Sample();
        item.setName("Hell");
        long id = source.insert(item);
        source.close();

        Intent intent = new Intent();
        intent.putExtra("data.id", id);
        activityTestRule.launchActivity(intent);
        onView(withId(R.id.edit_name)).perform(clearText()).perform(typeText("Hello"));
        onView(withId(R.id.action_save)).perform(click());

        source = new SampleDataSource(activityTestRule.getActivity());
        source.open();
        item = source.get(id);
        assertTrue(item.getName().equals("Hello"));
        List<Sample> list = source.list(false, null, "H", null);
        assertTrue(list.size() == 1);
        assertTrue(list.get(0).getName().equals("Hello"));

        Log.d(TAG, "ok");

        source.close();
    }

}

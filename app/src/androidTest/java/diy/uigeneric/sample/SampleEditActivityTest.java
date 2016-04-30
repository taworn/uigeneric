package diy.uigeneric.sample;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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

    private static final String TAG = SampleEditActivityTest.class.getSimpleName();

    @Rule
    public ActivityTestRule<SampleEditActivity> activityTestRule = new ActivityTestRule<>(SampleEditActivity.class, false, false);

    @Test
    public void testAdd() {
        long id;

        Log.d(TAG, "test on edit");

        // first, removing all records
        SampleDataSource source = new SampleDataSource(InstrumentationRegistry.getTargetContext());
        source.open();
        source.removeAll();

        // tests activity
        activityTestRule.launchActivity(null);
        onView(withId(R.id.edit_name)).perform(clearText()).perform(typeText("Aaa"));
        onView(withId(R.id.action_save)).perform(click());

        // checks record
        id = activityTestRule.getActivity().getItemId();
        Sample item = source.get(id);
        assertTrue(item.getName().equals("Aaa"));
        assertTrue(item.getDeleted() == null);

        source.close();
    }

    @Test
    public void testEdit() {
        long id;

        Log.d(TAG, "test on edit");

        // first, removing all records
        SampleDataSource source = new SampleDataSource(InstrumentationRegistry.getTargetContext());
        source.open();
        source.removeAll();

        // adds first record
        Sample item = new Sample();
        item.setName("Aaa");
        id = source.insert(item);
        assertTrue(item.getName().equals("Aaa"));
        assertTrue(item.getDeleted() == null);

        // tests activity
        Intent intent = new Intent();
        intent.putExtra("data.id", id);
        activityTestRule.launchActivity(intent);
        onView(withId(R.id.edit_name)).perform(clearText()).perform(typeText("Zzz"));
        onView(withId(R.id.action_save)).perform(click());

        // checks record, it's name changed to "Zzz"
        item = source.get(id);
        assertTrue(item.getName().equals("Zzz"));
        assertTrue(item.getDeleted() == null);

        source.close();
    }

}

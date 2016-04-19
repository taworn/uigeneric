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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SampleViewActivityTest {

    private static final String TAG = SampleViewActivityTest.class.getSimpleName();

    @Rule
    public ActivityTestRule<SampleViewActivity> activityTestRule = new ActivityTestRule<>(SampleViewActivity.class);

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
        onView(withId(R.id.action_edit)).perform(click());
        onView(withId(R.id.edit_name)).perform(typeText("a"));
        onView(withId(R.id.action_save)).perform(click());

        // checks record, it's name added 'a'
        item = source.get(id);
        assertTrue(item.getName().equals("Aaaa"));
        assertTrue(item.getDeleted() == null);

        source.close();
    }

    @Test
    public void testDelete() {
        long id;

        Log.d(TAG, "test on delete");

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
        onView(withId(R.id.action_delete)).perform(click());

        // checks record, it must be in trash
        item = source.get(id);
        assertTrue(item.getName().equals("Aaa"));
        assertTrue(item.getDeleted() != null);

        source.close();
    }

    @Test
    public void testEditDelete() {
        long id;

        Log.d(TAG, "test on edit then delete");

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
        onView(withId(R.id.action_edit)).perform(click());
        onView(withId(R.id.edit_name)).perform(typeText("a"));
        onView(withId(R.id.action_save)).perform(click());
        onView(withId(R.id.action_delete)).perform(click());

        // checks record, it's name added 'a' but it moved to trash
        item = source.get(id);
        assertTrue(item.getName().equals("Aaaa"));
        assertTrue(item.getDeleted() != null);

        source.close();
    }

}

package diy.uigeneric.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SampleDataSourceTest {

    private static final String TAG = "SampleDataSourceTest";

    @Test
    public void test() {
        Log.d(TAG, "SampleDataSourceTest.test()");

        Context context = InstrumentationRegistry.getTargetContext();

        SampleDataSource source = new SampleDataSource(context);
        source.open();

        // tests deleteAll() to clear data
        Log.d(TAG, "clears all data");
        source.removeAll();
        assertTrue(source.count() == 0);

        // tests insert
        Log.d(TAG, "tests insert, 3 records inserted");
        Sample itemInsert = new Sample();
        itemInsert.setName("Aaa");
        source.insert(itemInsert);
        itemInsert.setName("Bbb");
        source.insert(itemInsert);
        itemInsert.setName("Ccc");
        source.insert(itemInsert);

        // tests insert output
        List<Sample> list = source.list(null, null, null, null);
        for (Sample item : list) {
            assertTrue(item.getId() > 0 && item.getName().length() > 0);
        }
        assertTrue(source.count() == 3);

        // tests update
        Log.d(TAG, "tests update, 3 records still intact");
        long id = list.get(0).getId();
        Sample itemUpdate = source.get(id);
        itemUpdate.setName("Aaaa");
        itemUpdate.setCategory(Sample.CATEGORY_ARCHIVED);
        source.update(itemUpdate);
        assertTrue(source.count() == 3);

        // tests update output
        Sample itemUpdated = source.get(id);
        assertTrue(itemUpdated.getId() == id);
        assertTrue(itemUpdated.getName().equals("Aaaa"));
        assertTrue(itemUpdated.getCategory() == Sample.CATEGORY_ARCHIVED);
        assertNull(itemUpdated.getDeleted());
        assertTrue(source.count() == 3);

        // tests delete
        Log.d(TAG, "tests delete, one record deleted");
        source.delete(id);
        list = source.list(true, null, null, null);
        assertTrue(list.size() == 1);
        assertTrue(source.count() == 3);
        Sample itemDelete = list.get(0);
        assertTrue(itemDelete.getDeleted() != null);
        source.remove(id);
        assertTrue(source.count() == 2);

        // tests removeAll() to clear data
        Log.d(TAG, "clears all data, again");
        source.removeAll();
        assertTrue(source.count() == 0);

        source.close();

    }

}

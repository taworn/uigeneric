package diy.uigeneric.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SampleDataSourceTest {

    private static final String TAG = SampleDataSourceTest.class.getSimpleName();

    private SampleDataSource source = null;

    @Before
    public void before() {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleDataSource source = new SampleDataSource(context);
        source.open();
        source.removeAll();
        source.close();
    }

    @Test
    public void testBasic() {
        List<Sample> list;
        long id;
        Context context = InstrumentationRegistry.getTargetContext();
        SampleDataSource source = new SampleDataSource(context);
        source.open();

        // tests insert
        Log.d(TAG, "tests insert, 3 records inserted");
        Sample itemInsert = new Sample();
        itemInsert.setName("Aaa");
        source.insert(itemInsert);
        itemInsert.setName("Bbb");
        source.insert(itemInsert);
        itemInsert.setName("Ccc");
        source.insert(itemInsert);

        // views insert result
        list = source.list(null, null, null, null);
        assertTrue(list.size() == 3);
        list = source.list(false, null, null, null);
        assertTrue(list.size() == 3);
        assertTrue(source.count() == 3);
        assertTrue(source.count(false, null, null) == 3);
        list = source.list(null, null, null, null);
        for (Sample item : list) {
            Log.d(TAG, "item: " + item.getId() + "/" + item.getName());
            assertTrue(item.getId() > 0 && item.getName().length() > 0);
        }

        // tests update
        Log.d(TAG, "tests update, 3 records still intact");
        list = source.list(false, null, null, null);
        id = list.get(0).getId();
        Sample itemUpdate = source.get(id);
        itemUpdate.setName("Aaaa");
        itemUpdate.setCategory(Sample.CATEGORY_ARCHIVED);
        source.update(itemUpdate);

        // views update result
        Sample itemUpdated = source.get(id);
        assertTrue(itemUpdated.getId() == id);
        assertTrue(itemUpdated.getName().equals("Aaaa"));
        assertTrue(itemUpdated.getCategory() == Sample.CATEGORY_ARCHIVED);
        assertNull(itemUpdated.getDeleted());
        assertTrue(source.count() == 3);
        assertTrue(source.count(false, null, null) == 3);
        list = source.list(null, null, null, null);
        for (Sample item : list) {
            Log.d(TAG, "item: " + item.getId() + "/" + item.getName());
            assertTrue(item.getId() > 0 && item.getName().length() > 0);
        }

        source.close();
    }

    @Test
    public void testAdvance() {
        List<Sample> list;
        long id;
        Context context = InstrumentationRegistry.getTargetContext();
        SampleDataSource source = new SampleDataSource(context);
        source.open();

        // tests insert
        Log.d(TAG, "tests insert, 3 records inserted");
        Sample itemInsert = new Sample();
        itemInsert.setName("Aaa");
        source.insert(itemInsert);
        itemInsert.setName("Bbb");
        source.insert(itemInsert);
        itemInsert.setName("Ccc");
        source.insert(itemInsert);

        // views records
        list = source.list(null, null, null, null);
        assertTrue(list.size() == 3);
        list = source.list(false, null, null, null);
        assertTrue(list.size() == 3);
        assertTrue(source.count() == 3);
        assertTrue(source.count(false, null, null) == 3);

        // tests delete (move to trash)
        Log.d(TAG, "tests delete");
        list = source.list(false, null, null, null);
        id = list.get(0).getId();
        source.delete(id);
        list = source.list(null, null, null, null);
        assertTrue(list.size() == 3);
        list = source.list(false, null, null, null);
        assertTrue(list.size() == 2);
        list = source.list(true, null, null, null);
        assertTrue(list.size() == 1);
        assertTrue(source.count() == 3);
        assertTrue(source.count(false, null, null) == 2);
        assertTrue(source.count(true, null, null) == 1);

        // tests restore (record that move to trash)
        Log.d(TAG, "tests restore");
        list = source.list(true, null, null, null);
        id = list.get(0).getId();
        source.restore(id);
        list = source.list(null, null, null, null);
        assertTrue(list.size() == 3);
        list = source.list(false, null, null, null);
        assertTrue(list.size() == 3);
        list = source.list(true, null, null, null);
        assertTrue(list.size() == 0);
        assertTrue(source.count() == 3);
        assertTrue(source.count(false, null, null) == 3);
        assertTrue(source.count(true, null, null) == 0);

        // tests delete (move to trash)
        Log.d(TAG, "tests delete");
        list = source.list(false, null, null, null);
        id = list.get(0).getId();
        source.delete(id);
        list = source.list(null, null, null, null);
        assertTrue(list.size() == 3);
        list = source.list(false, null, null, null);
        assertTrue(list.size() == 2);
        list = source.list(true, null, null, null);
        assertTrue(list.size() == 1);
        assertTrue(source.count() == 3);
        assertTrue(source.count(false, null, null) == 2);
        assertTrue(source.count(true, null, null) == 1);

        // tests remove (delete real)
        Log.d(TAG, "tests remove");
        list = source.list(true, null, null, null);
        id = list.get(0).getId();
        source.remove(id);
        list = source.list(null, null, null, null);
        assertTrue(list.size() == 2);
        list = source.list(false, null, null, null);
        assertTrue(list.size() == 2);
        list = source.list(true, null, null, null);
        assertTrue(list.size() == 0);
        assertTrue(source.count() == 2);
        assertTrue(source.count(false, null, null) == 2);
        assertTrue(source.count(true, null, null) == 0);

        source.close();
    }

}

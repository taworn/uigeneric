package diy.uigeneric.test;

import android.test.AndroidTestCase;
import android.util.Log;

import java.util.List;

import diy.uigeneric.Generic;
import diy.uigeneric.GenericDataSource;

public class GenericDataSourceTest extends AndroidTestCase {

    public GenericDataSourceTest() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void test() throws Throwable {
        final String TAG = "GenericDataSourceTest";
        GenericDataSource source = new GenericDataSource(getContext());
        source.open();

        // tests deleteAll() to clear data
        Log.d(TAG, "clears all data");
        source.deleteAll();
        assertTrue(source.count() == 0);

        // tests insert
        Log.d(TAG, "tests insert, 3 records inserted");
        Generic itemInsert = new Generic();
        itemInsert.setName("Aaa");
        source.insert(itemInsert);
        itemInsert.setName("Bbb");
        source.insert(itemInsert);
        itemInsert.setName("Ccc");
        source.insert(itemInsert);

        // tests insert output
        List<Generic> list = source.list(null, null, null, null);
        for (Generic item : list) {
            assertTrue(item.getId() > 0 && item.getName().length() > 0);
        }
        assertTrue(source.count() == 3);

        // tests update
        Log.d(TAG, "tests update, 3 records still intact");
        long id = list.get(0).getId();
        Generic itemUpdate = source.get(id);
        itemUpdate.setName("Aaaa");
        itemUpdate.setCategory(Generic.CATEGORY_ARCHIVED);
        source.update(itemUpdate);
        assertTrue(source.count() == 3);

        // tests update output
        Generic itemUpdated = source.get(id);
        assertTrue(itemUpdated.getId() == id);
        assertTrue(itemUpdated.getName().equals("Aaaa"));
        assertTrue(itemUpdated.getCategory() == Generic.CATEGORY_ARCHIVED);
        assertNull(itemUpdated.getDeleted());
        assertTrue(source.count() == 3);

        // tests delete
        Log.d(TAG, "tests delete, one record deleted");
        source.delete(id);
        list = source.list(true, null, null, null);
        assertTrue(list.size() == 1);
        assertTrue(source.count() == 3);
        Generic itemDelete = list.get(0);
        assertTrue(itemDelete.getDeleted() != null);
        source.deleteReal(id);
        assertTrue(source.count() == 2);

        // tests deleteAll() to clear data
        Log.d(TAG, "clears all data, again");
        source.deleteAll();
        assertTrue(source.count() == 0);

        source.close();
    }

}

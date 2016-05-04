package diy.uigeneric.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SampleIndirectListTest {

    private static final String TAG = SampleIndirectListTest.class.getSimpleName();

    @Before
    public void before() {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleDataSource source = new SampleDataSource(context);
        source.open();
        source.removeAll();

        Sample sample;
        sample = new Sample();
        sample.setName("Aaa1");
        source.insert(sample);
        sample = new Sample();
        sample.setName("Aaa2");
        source.insert(sample);
        sample = new Sample();
        sample.setName("Aaa03");
        source.insert(sample);
        sample = new Sample();
        sample.setName("ZZZ");
        source.insert(sample);
        sample = new Sample();
        sample.setName("zzz");
        source.insert(sample);

        source.close();
    }

    @Test
    public void testFind() {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleIndirectList list = new SampleIndirectList();
        list.load(context, null, null, null, SampleIndirectList.SORT_AS_IS, false);

        // gets ids for every elements
        List<Long> idList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
            idList.add(list.get(i).getId());
        assertTrue(list.size() == 5);

        // finds and found
        Log.d(TAG, "find and found");
        assertTrue(list.find(idList.get(0)) == 0);
        assertTrue(list.find(idList.get(1)) == 1);
        assertTrue(list.find(idList.get(2)) == 2);
        assertTrue(list.find(idList.get(3)) == 3);
        assertTrue(list.find(idList.get(4)) == 4);

        // finds and not found
        Log.d(TAG, "find and not found");
        assertTrue(list.find(1000) < 0);
        assertTrue(list.find(5000) < 0);
        assertTrue(list.find(10000) < 0);
        assertTrue(list.find(50000) < 0);
    }

    @Test
    public void testAdd() {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleIndirectList list = new SampleIndirectList();
        list.load(context, null, null, null, SampleIndirectList.SORT_AS_IS, false);

        SampleDataSource source = new SampleDataSource(context);
        source.open();

        // adds
        Log.d(TAG, "adds without orientation switch");
        Sample item = new Sample();
        item.setName("XYZ");
        long id = source.insert(item);
        assertTrue(list.find(id) < 0);
        list.add(source.get(id));
        assertTrue(list.size() == 6);
        assertTrue(list.get(5).getName().equals("XYZ"));

        // adds and refreshes
        Log.d(TAG, "adds with emulation orientation switch (refresh)");
        item = new Sample();
        item.setName("UVW");
        id = source.insert(item);
        list.reload(context);
        assertFalse(list.find(id) < 0);
        assertTrue(list.size() == 7);
        assertTrue(list.get(6).getName().equals("UVW"));

        source.close();
    }

    @Test
    public void testEdit() {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleIndirectList list = new SampleIndirectList();
        list.load(context, null, null, null, SampleIndirectList.SORT_AS_IS, false);

        SampleDataSource source = new SampleDataSource(context);
        source.open();

        // edits
        Log.d(TAG, "edits without orientation switch");
        assertTrue(list.get(4).getName().equals("zzz"));
        Sample item = new Sample(list.get(4).getId());
        item.setName("XYZ");
        source.update(item);
        int i = list.find(item.getId());
        assertTrue(i >= 0);
        list.edit(i, source.get(item.getId()));
        assertTrue(list.size() == 5);
        assertTrue(list.get(4).getName().equals("XYZ"));

        // edits and refreshes
        Log.d(TAG, "edits with emulation orientation switch (refresh)");
        assertTrue(list.get(3).getName().equals("ZZZ"));
        item = new Sample(list.get(3).getId());
        item.setName("UVW");
        source.update(item);
        list.reload(context);
        assertTrue(list.size() == 5);
        assertTrue(list.get(3).getName().equals("UVW"));

        source.close();
    }

    @Test
    public void testSort() {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleIndirectList list = new SampleIndirectList();
        list.load(context, null, null, null, SampleIndirectList.SORT_AS_IS, false);

        // sorts as is
        Log.d(TAG, "test sort: as is");
        list.sort(SampleIndirectList.SORT_AS_IS, false);
        assertTrue(list.get(0).getName().equals("Aaa1"));
        assertTrue(list.get(1).getName().equals("Aaa2"));
        assertTrue(list.get(2).getName().equals("Aaa03"));
        assertTrue(list.get(3).getName().equals("ZZZ"));
        assertTrue(list.get(4).getName().equals("zzz"));
        assertTrue(list.size() == 5);
        Log.d(TAG, "test sort: as is, reverse");
        list.sort(SampleIndirectList.SORT_AS_IS, true);
        assertTrue(list.get(0).getName().equals("zzz"));
        assertTrue(list.get(1).getName().equals("ZZZ"));
        assertTrue(list.get(2).getName().equals("Aaa03"));
        assertTrue(list.get(3).getName().equals("Aaa2"));
        assertTrue(list.get(4).getName().equals("Aaa1"));
        assertTrue(list.size() == 5);

        // sorts name
        Log.d(TAG, "test sort: name");
        list.sort(SampleIndirectList.SORT_NAME, false);
        assertTrue(list.get(0).getName().equals("Aaa03"));
        assertTrue(list.get(1).getName().equals("Aaa1"));
        assertTrue(list.get(2).getName().equals("Aaa2"));
        assertTrue(list.get(3).getName().equals("ZZZ"));
        assertTrue(list.get(4).getName().equals("zzz"));
        assertTrue(list.size() == 5);
        Log.d(TAG, "test sort: name, reverse");
        list.sort(SampleIndirectList.SORT_NAME, true);
        assertTrue(list.get(0).getName().equals("zzz"));
        assertTrue(list.get(1).getName().equals("ZZZ"));
        assertTrue(list.get(2).getName().equals("Aaa2"));
        assertTrue(list.get(3).getName().equals("Aaa1"));
        assertTrue(list.get(4).getName().equals("Aaa03"));
        assertTrue(list.size() == 5);

        // sorts name ignore case
        Log.d(TAG, "test sort: name ignore case");
        list.sort(SampleIndirectList.SORT_NAME_IGNORE_CASE, false);
        assertTrue(list.get(0).getName().equals("Aaa03"));
        assertTrue(list.get(1).getName().equals("Aaa1"));
        assertTrue(list.get(2).getName().equals("Aaa2"));
        assertTrue(list.get(3).getName().equals("zzz"));
        assertTrue(list.get(4).getName().equals("ZZZ"));
        assertTrue(list.size() == 5);
        Log.d(TAG, "test sort: name ignore case, reverse");
        list.sort(SampleIndirectList.SORT_NAME_IGNORE_CASE, true);
        assertTrue(list.get(0).getName().equals("zzz"));
        assertTrue(list.get(1).getName().equals("ZZZ"));
        assertTrue(list.get(2).getName().equals("Aaa2"));
        assertTrue(list.get(3).getName().equals("Aaa1"));
        assertTrue(list.get(4).getName().equals("Aaa03"));
        assertTrue(list.size() == 5);

        // sorts natural
        Log.d(TAG, "test sort: name natural");
        list.sort(SampleIndirectList.SORT_NAME_NATURAL, false);
        assertTrue(list.get(0).getName().equals("Aaa1"));
        assertTrue(list.get(1).getName().equals("Aaa2"));
        assertTrue(list.get(2).getName().equals("Aaa03"));
        assertTrue(list.get(3).getName().equals("zzz"));
        assertTrue(list.get(4).getName().equals("ZZZ"));
        assertTrue(list.size() == 5);
        Log.d(TAG, "test sort: name natural, reverse");
        list.sort(SampleIndirectList.SORT_NAME_NATURAL, true);
        assertTrue(list.get(0).getName().equals("ZZZ"));
        assertTrue(list.get(1).getName().equals("zzz"));
        assertTrue(list.get(2).getName().equals("Aaa03"));
        assertTrue(list.get(3).getName().equals("Aaa2"));
        assertTrue(list.get(4).getName().equals("Aaa1"));
        assertTrue(list.size() == 5);
    }

    @Test
    public void testSearch() {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleIndirectList list = new SampleIndirectList();
        list.load(context, null, null, null, SampleIndirectList.SORT_NAME, false);

        Log.d(TAG, "test search: a");
        list.search(context, "a");
        assertTrue(list.get(0).getName().equals("Aaa03"));
        assertTrue(list.get(1).getName().equals("Aaa1"));
        assertTrue(list.get(2).getName().equals("Aaa2"));
        assertTrue(list.size() == 3);

        Log.d(TAG, "test search: aa");
        list.search(context, "aa");
        assertTrue(list.get(0).getName().equals("Aaa03"));
        assertTrue(list.get(1).getName().equals("Aaa1"));
        assertTrue(list.get(2).getName().equals("Aaa2"));
        assertTrue(list.size() == 3);

        Log.d(TAG, "test search: aaa");
        list.search(context, "aaa");
        list.sort(SampleIndirectList.SORT_NAME_NATURAL, false);
        assertTrue(list.get(0).getName().equals("Aaa1"));
        assertTrue(list.get(1).getName().equals("Aaa2"));
        assertTrue(list.get(2).getName().equals("Aaa03"));
        assertTrue(list.size() == 3);

        Log.d(TAG, "test search: aaa0");
        list.search(context, "aaa0");
        assertTrue(list.get(0).getName().equals("Aaa03"));
        assertTrue(list.size() == 1);

        Log.d(TAG, "test search: aaa0x");
        list.search(context, "aaa0x");
        assertTrue(list.size() == 0);
    }

}

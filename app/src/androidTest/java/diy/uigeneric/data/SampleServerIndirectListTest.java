package diy.uigeneric.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import diy.restlite.HttpRestLite;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SampleServerIndirectListTest {

    private static final String TAG = SampleServerIndirectListTest.class.getSimpleName();

    @Before
    public void before() {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleServerIndirectList list = new SampleServerIndirectList(context);
        list.removeAll();
    }

    @Test
    public void test() {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleServerIndirectList list = new SampleServerIndirectList(context);
        SampleServerDataSource source = new SampleServerDataSource(context);
        SampleServerDataSource.SampleHolder holder = new SampleServerDataSource.SampleHolder();
        Sample sample;
        HttpRestLite.Result result;

        // tests add
        sample = new Sample();
        sample.setName("Aaa");
        result = source.add(sample, holder);
        Log.d(TAG, "result.errorCode == " + result.errorCode);
        assertTrue(result.errorCode == 0);
        assertTrue(holder.sample.getName().equals("Aaa"));
        sample = new Sample();
        sample.setName("Bbb");
        result = source.add(sample, holder);
        Log.d(TAG, "result.errorCode == " + result.errorCode);
        assertTrue(result.errorCode == 0);
        assertTrue(holder.sample.getName().equals("Bbb"));
        sample = new Sample();
        sample.setName("Ccc");
        result = source.add(sample, holder);
        Log.d(TAG, "result.errorCode == " + result.errorCode);
        assertTrue(result.errorCode == 0);
        assertTrue(holder.sample.getName().equals("Ccc"));

        // tests list
        result = list.load(null, null, null, SampleServerIndirectList.SORT_NAME_NATURAL, true);
        Log.d(TAG, "result.errorCode == " + result.errorCode);
        assertTrue(result.errorCode == 0);
        assertTrue(list.get(0).getName().equals("Ccc"));
        assertTrue(list.get(1).getName().equals("Bbb"));
        assertTrue(list.get(2).getName().equals("Aaa"));
        assertTrue(list.size() == 3);
    }

}

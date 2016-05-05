package diy.uigeneric.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import diy.restlite.HttpRestLite;

@RunWith(AndroidJUnit4.class)
public class SampleServerIndirectListTest {

    private static final String TAG = SampleServerIndirectListTest.class.getSimpleName();

    @Test
    public void test() {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleServerDataSource source = new SampleServerDataSource(context);
        SampleServerIndirectList list = new SampleServerIndirectList(context);
        HttpRestLite.Result result;

    }

}

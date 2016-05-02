package diy.uigeneric.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import diy.restlite.HttpRestLite;

@RunWith(AndroidJUnit4.class)
public class SampleServerIndirectListTest {

    private static final String TAG = SampleServerIndirectListTest.class.getSimpleName();

    @Test
    public void test() {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleServerIndirectList list = new SampleServerIndirectList(context);
        HttpRestLite.Result result = list.load(false, null, "d", SampleServerIndirectList.SORT_AS_IS, true);

        Log.d(TAG, "result.errorCode = " + result.errorCode);
        for (int i = 0; i < list.size(); i++)
            Log.d(TAG, "data: " + list.get(i).getName());
    }

}

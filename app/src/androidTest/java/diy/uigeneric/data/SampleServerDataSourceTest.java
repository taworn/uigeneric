package diy.uigeneric.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import diy.restlite.HttpRestLite;

@RunWith(AndroidJUnit4.class)
public class SampleServerDataSourceTest {

    private static final String TAG = SampleServerDataSourceTest.class.getSimpleName();

    @Test
    public void test() throws JSONException {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleServerDataSource source = new SampleServerDataSource(context);
        HttpRestLite.HttpResult result = source.list(false, 0, "", "", null);
        Log.d(TAG, "result.errorCode: " + result.errorCode);
        Log.d(TAG, "result.json: " + result.json.toString(4));
    }

}

package diy.uigeneric.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import diy.restlite.HttpRestLite;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SampleServerDataSourceTest {

    private static final String TAG = SampleServerDataSourceTest.class.getSimpleName();

    @Before
    public void before() {
        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String serverAddress = pref.getString("server", "");
        if (serverAddress.length() > 0 && serverAddress.charAt(serverAddress.length() - 1) != '/')
            serverAddress = serverAddress.concat("/");
        HttpRestLite rest = new HttpRestLite();
        rest.execute(serverAddress + "api/sample/remove-all.php", "DELETE", null, null);
    }

    @Test
    public void test() {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleServerDataSource source = new SampleServerDataSource(context);
        SampleServerDataSource.SampleHolder holder = new SampleServerDataSource.SampleHolder();
        Sample sample;
        long id;
        HttpRestLite.Result result;

        // tests add
        sample = new Sample();
        sample.setName("Aaa");
        result = source.add(sample, holder);
        Log.d(TAG, "result.errorCode == " + result.errorCode);
        assertTrue(result.errorCode == 0);
        assertTrue(holder.sample.getName().equals("Aaa"));
        sample = holder.sample;
        id = sample.getId();
        Log.d(TAG, "test add, id: " + sample.getId() + ", name: " + sample.getName());

        // tests get
        result = source.get(id, holder);
        Log.d(TAG, "result.errorCode == " + result.errorCode);
        assertTrue(result.errorCode == 0);
        assertTrue(holder.sample.getName().equals("Aaa"));
        sample = holder.sample;
        Log.d(TAG, "test get, id: " + sample.getId() + ", name: " + sample.getName());

        // tests edit
        sample = holder.sample;
        sample.setName("Zzz");
        result = source.edit(sample, holder);
        Log.d(TAG, "result.errorCode == " + result.errorCode);
        assertTrue(result.errorCode == 0);
        assertTrue(holder.sample.getName().equals("Zzz"));
        sample = holder.sample;
        Log.d(TAG, "test edit, id: " + sample.getId() + ", name: " + sample.getName());
    }

}

package diy.uigeneric.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import diy.restlite.HttpRestLiteIndirect;

@RunWith(AndroidJUnit4.class)
public class SampleServerDataSourceTest {

    private static final String TAG = SampleServerDataSourceTest.class.getSimpleName();

    @Test
    public void testBasic() throws InterruptedException {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleServerDataSource source = new SampleServerDataSource(context);
        source.list(null, null, null, null, new HttpRestLiteIndirect.ResultListener() {
            @Override
            public void success(HttpRestLiteIndirect.HttpResult result) {
                try {
                    List<Sample> list = new ArrayList<Sample>();
                    if (result.data.has("ok") && result.data.getBoolean("ok")) {
                        Log.d(TAG, "data load successful");
                        JSONArray jsons = result.data.getJSONArray("items");
                        int length = jsons.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject json = jsons.getJSONObject(i);
                            Sample item = new Sample(json.getInt("id"));
                            item.setName(json.getString("name"));
                            item.setCategory(json.getInt("category"));
                            Log.d(TAG, "data load item: " + item.getId() + "/" + item.getName());
                            list.add(item);
                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(int code) {
            }
        }).execute(null, null);

        Thread.sleep(5000, 0);
    }

}

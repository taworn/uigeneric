package diy.uigeneric.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import diy.restlite.HttpRestLite;

@RunWith(AndroidJUnit4.class)
public class SampleServerIndirectListTest {

    private static final String TAG = SampleServerIndirectListTest.class.getSimpleName();

    @Test
    public void test() {
        Context context = InstrumentationRegistry.getTargetContext();
        SampleServerIndirectList list = new SampleServerIndirectList(context);
        HttpRestLite.Result result;
        List<Long> idList = new ArrayList<>();

        idList.clear();
        result = list.load(false, null, null, SampleServerIndirectList.SORT_NAME_NATURAL, false);
        Log.d(TAG, "result.errorCode = " + result.errorCode);
        for (int i = 0; i < list.size(); i++) {
            Log.d(TAG, "data: " + list.get(i).getName());
            idList.add(list.get(i).getId());
        }

        Log.d(TAG, "idList.size(): " + idList.size());
        result = list.delete(idList);
        Log.d(TAG, "result.errorCode = " + result.errorCode);
        for (int i = 0; i < list.size(); i++) {
            Log.d(TAG, "data: " + list.get(i).getName());
        }

        idList.clear();
        result = list.load(true, null, null, SampleServerIndirectList.SORT_NAME_NATURAL, false);
        Log.d(TAG, "result.errorCode = " + result.errorCode);
        for (int i = 0; i < list.size(); i++) {
            Log.d(TAG, "data: " + list.get(i).getName());
            idList.add(list.get(i).getId());
        }

        Log.d(TAG, "idList.size(): " + idList.size());
        result = list.restore(idList);
        Log.d(TAG, "result.errorCode = " + result.errorCode);
        for (int i = 0; i < list.size(); i++) {
            Log.d(TAG, "data: " + list.get(i).getName());
        }

    }

}

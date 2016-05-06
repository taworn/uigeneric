package diy.uigeneric.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import diy.restlite.HttpRestLite;

/**
 * A class to manage Sample table on server.  It's provide get, add and edit functions easier.
 */
public class SampleServerDataSource {

    private static final String TAG = SampleServerDataSource.class.getSimpleName();

    public class SampleHolder {
        public Sample sample = null;
    }

    public interface ResultListener {
        /**
         * Calls when HTTP execute finished.
         */
        void finish(HttpRestLite.Result result, @NonNull SampleHolder holder);
    }

    // base server address
    private String serverAddress;
    private HttpRestLite rest;

    /**
     * Constructs to manage table.
     */
    public SampleServerDataSource(@NonNull Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        serverAddress = pref.getString("server", "");
        if (serverAddress.length() > 0 && serverAddress.charAt(serverAddress.length() - 1) != '/')
            serverAddress = serverAddress.concat("/");
        rest = new HttpRestLite();
    }

    /**
     * Gets data.
     */
    public HttpRestLite.Result get(long id, @NonNull SampleHolder holder) {
        HttpRestLite.Result result = rest.execute(serverAddress + "api/sample/get.php/" + id, "GET", null, null);
        if (result.errorCode == 0) {
            holder.sample = loading(result);
        }
        return result;
    }

    /**
     * Gets data async.
     */
    public void get(long id, @NonNull final ResultListener listener) {
        rest.execute(serverAddress + "api/sample/get.php/" + id, "GET", null, null, new HttpRestLite.ResultListener() {
            @Override
            public void finish(HttpRestLite.Result result) {
                SampleHolder holder = new SampleHolder();
                if (result.errorCode == 0) {
                    holder.sample = loading(result);
                }
                listener.finish(result, holder);
            }
        });
    }

    /**
     * Adds data.
     */
    public HttpRestLite.Result add(@NonNull Sample sample) {
        Map<String, String> params = new HashMap<>();
        params.put("icon", iconToString(sample.getIcon()));
        params.put("name", sample.getName());
        params.put("detail", sample.getDetail());
        params.put("category", String.valueOf(sample.getCategory()));
        return rest.execute(serverAddress + "api/sample/add.php", "POST", params, null);
    }

    /**
     * Adds data async.
     */
    public void add(@NonNull Sample sample, @NonNull final ResultListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put("icon", iconToString(sample.getIcon()));
        params.put("name", sample.getName());
        params.put("detail", sample.getDetail());
        params.put("category", String.valueOf(sample.getCategory()));
        rest.execute(serverAddress + "api/sample/add.php", "POST", params, null, new HttpRestLite.ResultListener() {
            @Override
            public void finish(HttpRestLite.Result result) {
                SampleHolder holder = new SampleHolder();
                if (result.errorCode == 0) {
                    holder.sample = loading(result);
                }
                listener.finish(result, holder);
            }
        });
    }

    /**
     * Edits data.
     */
    public HttpRestLite.Result edit(@NonNull Sample sample) {
        Map<String, String> params = new HashMap<>();
        params.put("icon", iconToString(sample.getIcon()));
        params.put("name", sample.getName());
        params.put("detail", sample.getDetail());
        params.put("category", String.valueOf(sample.getCategory()));
        return rest.execute(serverAddress + "api/sample/edit.php/" + sample.getId(), "PUT", params, null);
    }

    /**
     * Edits data async.
     */
    public void edit(@NonNull Sample sample, @NonNull final ResultListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put("icon", iconToString(sample.getIcon()));
        params.put("name", sample.getName());
        params.put("detail", sample.getDetail());
        params.put("category", String.valueOf(sample.getCategory()));
        rest.execute(serverAddress + "api/sample/edit.php/" + sample.getId(), "PUT", params, null, new HttpRestLite.ResultListener() {
            @Override
            public void finish(HttpRestLite.Result result) {
                SampleHolder holder = new SampleHolder();
                if (result.errorCode == 0) {
                    holder.sample = loading(result);
                }
                listener.finish(result, holder);
            }
        });
    }

    /**
     * Cancels the HTTP operation.
     */
    public void cancel() {
        rest.cancel();
    }

    public static Bitmap iconFromString(String string) {
        byte[] byteArray = Base64.decode(string, Base64.DEFAULT);
        return SampleDataSource.iconFromBlob(byteArray);
    }

    public static String iconToString(Bitmap icon) {
        byte[] byteArray = SampleDataSource.blobFromIcon(icon);
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Sample loading(HttpRestLite.Result result) {
        try {
            if (result.json.has("ok") && result.json.getBoolean("ok")) {
                //Log.d(TAG, result.json.toString(4));
                JSONObject item = result.json.getJSONObject("item");
                Sample sample;
                if (item.has("id"))
                    sample = new Sample(item.getInt("id"));
                else
                    sample = new Sample();
                sample.setIcon(iconFromString(item.getString("icon")));
                sample.setName(item.getString("name"));
                sample.setDetail(item.getString("detail"));
                sample.setCategory(item.getInt("category"));
                sample.setDeleted(item.isNull("deleted") ? null : SampleDataSource.dateFromLong(item.getLong("deleted")));
                Log.d(TAG, "data load item: " + sample.getId() + "/" + sample.getName());
                return sample;
            }
            else {
                result.errorCode = HttpRestLite.ERROR_CUSTOM;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            result.errorCode = HttpRestLite.ERROR_JSON;
        }
        return null;
    }

}

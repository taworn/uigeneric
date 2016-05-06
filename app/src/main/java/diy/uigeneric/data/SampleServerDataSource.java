package diy.uigeneric.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    public static class SampleHolder {
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
        return call(serverAddress + "api/sample/get.php/" + id, "GET", null, holder);
    }

    /**
     * Gets data async.
     */
    public void get(long id, @NonNull ResultListener listener) {
        call(serverAddress + "api/sample/get.php/" + id, "GET", null, listener);
    }

    /**
     * Adds data.
     */
    public HttpRestLite.Result add(@NonNull Sample sample, @NonNull SampleHolder holder) {
        Map<String, String> params = collectParameters(sample);
        return call(serverAddress + "api/sample/add.php", "POST", params, holder);
    }

    /**
     * Adds data async.
     */
    public void add(@NonNull Sample sample, @NonNull final ResultListener listener) {
        Map<String, String> params = collectParameters(sample);
        call(serverAddress + "api/sample/add.php", "POST", params, listener);
    }

    /**
     * Edits data.
     */
    public HttpRestLite.Result edit(@NonNull Sample sample, @NonNull SampleHolder holder) {
        Map<String, String> params = collectParameters(sample);
        return call(serverAddress + "api/sample/edit.php/" + sample.getId(), "PUT", params, holder);
    }

    /**
     * Edits data async.
     */
    public void edit(@NonNull Sample sample, @NonNull ResultListener listener) {
        Map<String, String> params = collectParameters(sample);
        call(serverAddress + "api/sample/edit.php/" + sample.getId(), "PUT", params, listener);
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
        return byteArray != null ? Base64.encodeToString(byteArray, Base64.DEFAULT) : "";
    }

    private Map<String, String> collectParameters(@Nullable Sample sample) {
        Map<String, String> params = new HashMap<>();
        if (sample != null) {
            params.put("icon", iconToString(sample.getIcon()));
            params.put("name", sample.getName());
            params.put("detail", sample.getDetail());
            params.put("category", String.valueOf(sample.getCategory()));
        }
        return params;
    }

    private HttpRestLite.Result call(@NonNull String url, @NonNull String request, @Nullable Map<String, String> params, @NonNull SampleHolder holder) {
        HttpRestLite.Result result = rest.execute(url, request, params, null);
        if (result.errorCode == 0) {
            holder.sample = loading(result);
        }
        return result;
    }

    private void call(@NonNull String url, @NonNull String request, @Nullable Map<String, String> params, @NonNull final ResultListener listener) {
        rest.execute(url, request, params, null, new HttpRestLite.ResultListener() {
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

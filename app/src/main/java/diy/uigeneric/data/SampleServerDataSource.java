package diy.uigeneric.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import diy.restlite.HttpRestLite;

/**
 * A class to manage Sample table on server.  It's provide list, insert, update, delete and
 * utilities functions easier.
 */
public class SampleServerDataSource {

    private static final String TAG = SampleServerDataSource.class.getSimpleName();

    private String serverAddress = null;
    private String cookie = null;
    private HttpRestLite rest = null;

    /**
     * Constructs to manage table.
     */
    public SampleServerDataSource(@NonNull Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        serverAddress = pref.getString("server", "");
        if (serverAddress.length() > 0) {
            if (serverAddress.charAt(serverAddress.length() - 1) != '/')
                serverAddress.concat("/");
        }
        Log.d(TAG, "server: " + serverAddress);

        rest = new HttpRestLite();
    }

    /**
     * Converts from bitmap to byte array.
     *
     * @param icon Bitmap data.
     * @return Byte array that converted from bitmap.
     */
    public static byte[] blobFromIcon(@Nullable Bitmap icon) {
        if (icon != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            double ratio = Math.min(128.0 / icon.getWidth(), 128.0 / icon.getHeight());
            double w = icon.getWidth() * ratio;
            double h = icon.getHeight() * ratio;
            icon = Bitmap.createScaledBitmap(icon, (int) w, (int) h, true);
            icon.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }
        else
            return null;
    }

    /**
     * Converts from byte array to bitmap.
     *
     * @param byteArray Byte array data.
     * @return Bitmap that converted from byte array.
     */
    public static Bitmap iconFromBlob(@Nullable byte[] byteArray) {
        if (byteArray != null)
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        else
            return null;
    }

    /**
     * Converts from date/time to long.
     *
     * @param datetime Datetime data.
     * @return Long, as milli-seconds, that converted from datetime.
     */
    public static Long longFromDate(@Nullable Calendar datetime) {
        if (datetime != null)
            return datetime.getTimeInMillis();
        else
            return null;
    }

    /**
     * Converts from long to date/time.
     *
     * @param milli Long, as milli-seconds, data.
     * @return Datetime that converted from long.
     */
    public static Calendar dateFromLong(@Nullable Long milli) {
        if (milli != null && milli != 0) {
            Calendar c = new GregorianCalendar(Locale.US);
            c.setTimeInMillis(milli);
            return c;
        }
        else
            return null;
    }

    /**
     * Opens the table.
     */
    public void open() {
    }

    /**
     * Closes the table.
     */
    public void close() {
    }

    /**
     * Counts data from table.
     */
    public int count() {
        return count(null, null, null);
    }

    /**
     * Counts data from table.
     *
     * @param deleted  Delete flag, can be null.
     * @param category Category to select, can be null.
     * @param search   Search data, can be null.
     */
    public int count(@Nullable Boolean deleted, @Nullable Integer category, @Nullable String search) {
        return 0;
    }

    /**
     * Lists data from table.  The data will NOT be included detail field.
     *
     * @param deleted  Delete flag, can be null.
     * @param category Category to select, can be null.
     * @param search   Search data, can be null.
     * @param orderBy  Order by string, can be null.
     * @return All data in list that matched records in table.
     */
    public HttpRestLite.HttpResult list(@Nullable Boolean deleted, @Nullable Integer category, @Nullable String search, @Nullable String orderBy, @Nullable HttpRestLite.ResultListener listener) {
        Map<String, String> params = new HashMap<>();
        if (deleted != null)
            params.put("deleted", !deleted ? "0" : "1");
        if (category != null)
            params.put("category", category.toString());
        if (search != null)
            params.put("search", search);
        if (orderBy != null)
            params.put("orderBy", orderBy);
        rest.setUrl(serverAddress + "api/sample/list.php", "POST");
        return rest.execute(params, cookie, listener);
    }

    /**
     * Gets data from table.
     *
     * @param id Primary key.
     * @return Data that matched the id.
     */
    public Sample get(long id) {
        return null;
    }

    /**
     * Inserts data into table.
     *
     * @return Returns new id.
     */
    public long insert(@NonNull Sample item) {
        return 0;
    }

    /**
     * Updates data to table.
     */
    public void update(@NonNull Sample item) {
    }

    /**
     * Restores a record from trash.
     */
    public void restore(long id) {
    }

    /**
     * Restores list records from trash.
     */
    public void restoreList(@NonNull List<Long> list) {
    }

    /**
     * Deletes a record from table.  In fact, it just moves to trash.
     */
    public void delete(long id) {
    }

    /**
     * Deletes list records from table.  In fact, it just moves to trash.
     */
    public void deleteList(@NonNull List<Long> list) {
    }

    /**
     * Deletes a record from trash.
     */
    public void remove(long id) {
    }

    /**
     * Deletes list records from trash.
     */
    public void removeList(@NonNull List<Long> list) {
    }

    /**
     * Deletes too old data in trash.
     */
    public void removeTrash(long deleteTime) {
    }

    /**
     * Deletes ALL data from table, includes trash.
     */
    public void removeAll() {
    }

}

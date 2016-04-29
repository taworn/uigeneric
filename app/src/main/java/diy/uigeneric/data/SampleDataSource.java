package diy.uigeneric.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * A class to manage Sample table.  It's provide list, insert, update, delete and utilities
 * functions easier.
 */
public class SampleDataSource {

    private SampleOpenHelper helper = null;
    private SQLiteDatabase database = null;
    private SQLiteStatement insertStatement = null;
    private SQLiteStatement updateStatement = null;

    /**
     * Constructs to manage table.
     */
    public SampleDataSource(@NonNull Context context) {
        helper = new SampleOpenHelper(context);
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
        database = helper.getWritableDatabase();
    }

    /**
     * Closes the table.
     */
    public void close() {
        helper.close();
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
        // prepares query and parameters
        String query = "SELECT COUNT(*)"
                + " FROM sample"
                + " WHERE 0 = 0";
        List<String> parameters = new ArrayList<>();
        if (deleted != null) {
            if (!deleted)
                query += " AND deleted IS NULL";
            else
                query += " AND deleted IS NOT NULL";
        }
        if (category != null)
            query += " AND category = " + category;
        if (search != null) {
            query += " AND name LIKE ?";
            parameters.add('%' + search + '%');
        }

        // executes query
        Cursor cursor = database.rawQuery(query, parameters.toArray(new String[parameters.size()]));

        // returns
        int count;
        cursor.moveToFirst();
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0)
            count = cursor.getInt(0);
        else
            count = 0;
        cursor.close();
        return count;
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
    public List<Sample> list(@Nullable Boolean deleted, @Nullable Integer category, @Nullable String search, @Nullable String orderBy) {
        ArrayList<Sample> list = new ArrayList<>();

        // prepares query and parameters
        String query = "SELECT id, icon, name, category, deleted"
                + " FROM sample"
                + " WHERE 0 = 0";
        List<String> parameters = new ArrayList<>();
        if (deleted != null) {
            if (!deleted)
                query += " AND deleted IS NULL";
            else
                query += " AND deleted IS NOT NULL";
        }
        if (category != null)
            query += " AND category = " + category;
        if (search != null) {
            query += " AND name LIKE ?";
            parameters.add('%' + search + '%');
        }
        if (orderBy != null)
            query += " ORDER BY " + orderBy;

        // executes query
        Cursor cursor = database.rawQuery(query, parameters.toArray(new String[parameters.size()]));

        // copied data to list
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Sample item = new Sample(cursor.getLong(0));
                item.setIcon(iconFromBlob(cursor.getBlob(1)));
                item.setName(cursor.getString(2));
                item.setCategory(cursor.getInt(3));
                item.setDeleted(dateFromLong(cursor.getLong(4)));
                list.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return list;
    }

    /**
     * Gets data from table.
     *
     * @param id Primary key.
     * @return Data that matched the id.
     */
    public Sample get(long id) {
        // prepares query
        String query = "SELECT id, icon, name, detail, category, deleted"
                + " FROM sample"
                + " WHERE id = " + id;

        // executes query
        Cursor cursor = database.rawQuery(query, null);

        // copied data
        if (cursor.moveToFirst()) {
            Sample item = new Sample(cursor.getLong(0));
            item.setIcon(iconFromBlob(cursor.getBlob(1)));
            item.setName(cursor.getString(2));
            item.setDetail(cursor.getString(3));
            item.setCategory(cursor.getInt(4));
            item.setDeleted(dateFromLong(cursor.getLong(5)));
            cursor.close();
            return item;
        }
        else
            return null;
    }

    /**
     * Inserts data into table.
     *
     * @return Returns new id.
     */
    public long insert(@NonNull Sample item) {
        if (insertStatement == null)
            insertStatement = database.compileStatement("INSERT INTO sample(id, icon, name, detail, category, deleted) VALUES(?, ?, ?, ?, ?, ?)");
        insertStatement.clearBindings();
        insertStatement.bindNull(1);
        if (item.getIcon() != null)
            insertStatement.bindBlob(2, blobFromIcon(item.getIcon()));
        else
            insertStatement.bindNull(2);
        insertStatement.bindString(3, item.getName());
        insertStatement.bindString(4, item.getDetail());
        insertStatement.bindLong(5, item.getCategory());
        if (item.getDeleted() != null)
            insertStatement.bindLong(6, longFromDate(item.getDeleted()));
        else
            insertStatement.bindNull(6);
        return insertStatement.executeInsert();
    }

    /**
     * Updates data to table.
     */
    public void update(@NonNull Sample item) {
        if (updateStatement == null)
            updateStatement = database.compileStatement("UPDATE sample SET icon = ?"
                    + ", name = ?"
                    + ", detail = ?"
                    + ", category = ?"
                    + ", deleted = ?"
                    + " WHERE id = ?");
        updateStatement.clearBindings();
        if (item.getIcon() != null)
            updateStatement.bindBlob(1, blobFromIcon(item.getIcon()));
        else
            updateStatement.bindNull(1);
        updateStatement.bindString(2, item.getName());
        updateStatement.bindString(3, item.getDetail());
        updateStatement.bindLong(4, item.getCategory());
        if (item.getDeleted() != null)
            updateStatement.bindLong(5, longFromDate(item.getDeleted()));
        else
            updateStatement.bindNull(5);
        updateStatement.bindLong(6, item.getId());
        updateStatement.executeUpdateDelete();
    }

    /**
     * Restores a record from trash.
     */
    public void restore(long id) {
        SQLiteStatement statement = database.compileStatement("UPDATE sample SET deleted = NULL WHERE id = ?");
        statement.clearBindings();
        statement.bindLong(1, id);
        statement.executeUpdateDelete();
    }

    /**
     * Restores list records from trash.
     */
    public void restoreList(@NonNull List<Long> list) {
        SQLiteStatement statement = database.compileStatement("UPDATE sample SET deleted = NULL WHERE id = ?");
        statement.clearBindings();
        for (long id : list) {
            statement.bindLong(1, id);
            statement.executeUpdateDelete();
        }
    }

    /**
     * Deletes a record from table.  In fact, it just moves to trash.
     */
    public void delete(long id) {
        SQLiteStatement statement = database.compileStatement("UPDATE sample SET deleted = ? WHERE id = ?");
        statement.clearBindings();
        statement.bindLong(1, longFromDate(new GregorianCalendar(Locale.US)));
        statement.bindLong(2, id);
        statement.executeUpdateDelete();
    }

    /**
     * Deletes list records from table.  In fact, it just moves to trash.
     */
    public void deleteList(@NonNull List<Long> list) {
        SQLiteStatement statement = database.compileStatement("UPDATE sample SET deleted = ? WHERE id = ?");
        statement.clearBindings();
        statement.bindLong(1, longFromDate(new GregorianCalendar(Locale.US)));
        for (long id : list) {
            statement.bindLong(2, id);
            statement.executeUpdateDelete();
        }
    }

    /**
     * Deletes a record from trash.
     */
    public void remove(long id) {
        SQLiteStatement statement = database.compileStatement("DELETE FROM sample WHERE deleted IS NOT NULL AND id = ?");
        statement.clearBindings();
        statement.bindLong(1, id);
        statement.executeUpdateDelete();
    }

    /**
     * Deletes list records from trash.
     */
    public void removeList(@NonNull List<Long> list) {
        SQLiteStatement statement = database.compileStatement("DELETE FROM sample WHERE deleted IS NOT NULL AND id = ?");
        statement.clearBindings();
        for (long id : list) {
            statement.bindLong(1, id);
            statement.executeUpdateDelete();
        }
    }

    /**
     * Deletes too old data in trash.
     */
    public void removeTrash(long deleteTime) {
        long time = new GregorianCalendar(Locale.US).getTimeInMillis();
        time -= deleteTime;
        SQLiteStatement statement = database.compileStatement("DELETE FROM sample WHERE deleted < ?");
        statement.clearBindings();
        statement.bindLong(1, time);
        statement.executeUpdateDelete();
    }

    /**
     * Deletes ALL data from table, includes trash.
     */
    public void removeAll() {
        database.delete("sample", null, null);
    }

}

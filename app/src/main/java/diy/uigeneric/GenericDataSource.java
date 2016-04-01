package diy.uigeneric;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The class to manage database.  It's provide list, insert, update and delete easier.
 */
public class GenericDataSource {

    private GenericOpenHelper helper = null;
    private SQLiteDatabase database = null;
    private SQLiteStatement insertStatement = null;
    private SQLiteStatement updateStatement = null;

    /**
     * Creates a helper class to manage database.
     */
    public GenericDataSource(Context context) {
        helper = new GenericOpenHelper(context);
    }

    /**
     * A utility to converts from bitmap to byte array.
     *
     * @param icon Bitmap data.
     * @return Byte array that converted from bitmap.
     */
    public static byte[] blobFromIcon(Bitmap icon) {
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
     * A utility to converts from byte array to bitmap.
     *
     * @param byteArray Byte array data.
     * @return Bitmap that converted from byte array.
     */
    public static Bitmap iconFromBlob(byte[] byteArray) {
        if (byteArray != null)
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        else
            return null;
    }

    /**
     * A utility to converts from date/time to long.
     *
     * @param datetime Datetime data.
     * @return Long, as milli-seconds, that converted from date/time.
     */
    public static Long longFromDate(Date datetime) {
        if (datetime != null)
            return datetime.getTime();
        else
            return null;
    }

    /**
     * A utility to converts from long to date/time.
     *
     * @param milli Long, as milli-seconds, data.
     * @return Datetime that converted from long.
     */
    public static Date dateFromLong(Long milli) {
        if (milli != null && milli != 0)
            return new Date(milli);
        else
            return null;
    }

    /**
     * Opens the database.
     */
    public void open() {
        database = helper.getWritableDatabase();
    }

    /**
     * Closes the database.
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
    public int count(Boolean deleted, Integer category, String search) {
        // prepares query and parameters
        String query = "SELECT COUNT(*)"
                + " FROM generic"
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
    public List<Generic> list(Boolean deleted, Integer category, String search, String orderBy) {
        ArrayList<Generic> list = new ArrayList<>();

        // prepares query and parameters
        String query = "SELECT id, icon, name, category, deleted"
                + " FROM generic"
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
                Generic item = new Generic(cursor.getLong(0));
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
    public Generic get(long id) {
        // prepares query
        String query = "SELECT id, icon, name, category, deleted, detail"
                + " FROM generic"
                + " WHERE id = " + id;

        // executes query
        Cursor cursor = database.rawQuery(query, null);

        // copied data to item
        if (!cursor.moveToFirst())
            throw new IllegalArgumentException("Item not found.");
        Generic item = new Generic(cursor.getLong(0));
        item.setIcon(iconFromBlob(cursor.getBlob(1)));
        item.setName(cursor.getString(2));
        item.setCategory(cursor.getInt(3));
        item.setDeleted(dateFromLong(cursor.getLong(4)));
        item.setDetail(cursor.getString(5));
        cursor.close();
        return item;
    }

    /**
     * Inserts data into table.
     *
     * @return Returns new id.
     */
    public long insert(Generic item) {
        if (insertStatement == null)
            insertStatement = database.compileStatement("INSERT INTO generic(id, icon, name, category, deleted, detail)"
                    + " VALUES(?, ?, ?, ?, ?, ?)");
        insertStatement.clearBindings();
        insertStatement.bindNull(1);
        if (item.getIcon() != null)
            insertStatement.bindBlob(2, blobFromIcon(item.getIcon()));
        else
            insertStatement.bindNull(2);
        insertStatement.bindString(3, item.getName());
        insertStatement.bindLong(4, item.getCategory());
        if (item.getDeleted() != null)
            insertStatement.bindLong(5, longFromDate(item.getDeleted()));
        else
            insertStatement.bindNull(5);
        insertStatement.bindString(6, item.getDetail());
        return insertStatement.executeInsert();
    }

    /**
     * Updates data to table.
     */
    public void update(Generic item) {
        if (updateStatement == null)
            updateStatement = database.compileStatement("UPDATE generic SET icon = ?"
                    + ", name = ?"
                    + ", category = ?"
                    + ", deleted = ?"
                    + ", detail = ?"
                    + " WHERE id = ?");
        updateStatement.clearBindings();
        if (item.getIcon() != null)
            updateStatement.bindBlob(1, blobFromIcon(item.getIcon()));
        else
            updateStatement.bindNull(1);
        updateStatement.bindString(2, item.getName());
        updateStatement.bindLong(3, item.getCategory());
        if (item.getDeleted() != null)
            updateStatement.bindLong(4, longFromDate(item.getDeleted()));
        else
            updateStatement.bindNull(4);
        updateStatement.bindString(5, item.getDetail());
        updateStatement.bindLong(6, item.getId());
        updateStatement.executeUpdateDelete();
    }

    /**
     * Restores a record from trash.
     */
    public void restore(long id) {
        SQLiteStatement statement = database.compileStatement("UPDATE generic SET deleted = NULL WHERE id = ?");
        statement.clearBindings();
        statement.bindLong(1, id);
        statement.executeUpdateDelete();
    }

    /**
     * Restores list records from trash.
     */
    public void restoreList(List<Long> list) {
        SQLiteStatement statement = database.compileStatement("UPDATE generic SET deleted = NULL WHERE id = ?");
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
        SQLiteStatement statement = database.compileStatement("UPDATE generic SET deleted = ? WHERE id = ?");
        statement.clearBindings();
        statement.bindLong(1, longFromDate(new Date()));
        statement.bindLong(2, id);
        statement.executeUpdateDelete();
    }

    /**
     * Deletes list records from table.  In fact, it just moves to trash.
     */
    public void deleteList(List<Long> list) {
        SQLiteStatement statement = database.compileStatement("UPDATE generic SET deleted = ? WHERE id = ?");
        statement.clearBindings();
        statement.bindLong(1, longFromDate(new Date()));
        for (long id : list) {
            statement.bindLong(2, id);
            statement.executeUpdateDelete();
        }
    }

    /**
     * Deletes a record from trash.
     */
    public void deleteReal(long id) {
        SQLiteStatement statement = database.compileStatement("DELETE FROM generic WHERE deleted IS NOT NULL AND id = ?");
        statement.clearBindings();
        statement.bindLong(1, id);
        statement.executeUpdateDelete();
    }

    /**
     * Deletes list records from trash.
     */
    public void deleteListReal(List<Long> list) {
        SQLiteStatement statement = database.compileStatement("DELETE FROM generic WHERE deleted IS NOT NULL AND id = ?");
        statement.clearBindings();
        for (long id : list) {
            statement.bindLong(1, id);
            statement.executeUpdateDelete();
        }
    }

    /**
     * Deletes too old data in trash.
     */
    public void deleteFromTrash() {
        long time = new Date().getTime();
        time -= 3 * 24 * 60 * 60 * 1000;
        SQLiteStatement statement = database.compileStatement("DELETE FROM generic WHERE deleted < ?");
        statement.clearBindings();
        statement.bindLong(1, time);
        statement.executeUpdateDelete();
    }

    /**
     * Deletes ALL data from table, includes trash.
     */
    public void deleteAll() {
        database.delete("generic", null, null);
    }

}

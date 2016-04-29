package diy.uigeneric.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.preference.PreferenceManager;

/**
 * A helper class to manage database creation and version management.
 */
public class SampleOpenHelper extends SQLiteOpenHelper {

    private static String getDatabase(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String database = pref.getString("database", "0");
        if (database.equals("") || database.equals("0"))
            return "uigeneric.db";
        else
            return context.getExternalFilesDir(null) + "/uigeneric.db";
    }

    public SampleOpenHelper(Context context) {
        super(context, getDatabase(context), null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query = "CREATE TABLE sample(id INTEGER PRIMARY KEY"
                + ", icon BLOB"
                + ", name TEXT NOT NULL"
                + ", detail TEXT DEFAULT ''"
                + ", category INT DEFAULT 0"
                + ", deleted INT)";
        database.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2) {
        database.execSQL("DROP TABLE IF EXISTS generic");
        onCreate(database);
    }

}

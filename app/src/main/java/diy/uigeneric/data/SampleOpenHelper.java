package diy.uigeneric.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * The helper class to creation of database and version management.
 */
public class SampleOpenHelper extends SQLiteOpenHelper {

    public SampleOpenHelper(Context context) {
        //super(context, "uigeneric.db", null, 1);
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/uigeneric.db", null, 1);
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

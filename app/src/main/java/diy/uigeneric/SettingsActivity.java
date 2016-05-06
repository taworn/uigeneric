package diy.uigeneric;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new SettingsFragment())
                .commit();

        Log.d(TAG, "onCreate settings");
        writeLog();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy settings");
        writeLog();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void writeLog() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String database = sharedPref.getString("database", "0");
        String server = sharedPref.getString("server", "");
        String username = sharedPref.getString("username", "");
        String email = sharedPref.getString("email", "");
        Log.d(TAG, "database: " + database);
        Log.d(TAG, "server: " + server);
        Log.d(TAG, "username: " + username);
        Log.d(TAG, "email: " + email);
    }

}

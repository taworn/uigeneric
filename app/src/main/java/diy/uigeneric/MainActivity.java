package diy.uigeneric;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import diy.uigeneric.sample.SampleListActivity;
import diy.uigeneric.sampleserver.SampleServerListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void onSampleClick(View view) {
        startActivity(new Intent(this, SampleListActivity.class));
    }

    public void onSampleServerClick(View view) {
        startActivity(new Intent(this, SampleServerListActivity.class));
    }

    public void onSettingsClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void onAboutClick(View view) {
        String message = getResources().getString(R.string.about_dialog_message) + " " + getResources().getString(R.string.version);
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_android_black_24dp)
                .setTitle(R.string.about_dialog_title)
                .setMessage(message)
                .setPositiveButton(R.string.about_dialog_positive, null)
                .show();
    }

}

package diy.uigeneric;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import diy.uigeneric.sample.SampleListActivity;

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
    }

    public void onSettingsClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void onAboutClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about_dialog_title)
                .setMessage(R.string.about_dialog_message)
                .setPositiveButton(R.string.about_dialog_positive, null)
                .show();
    }

}

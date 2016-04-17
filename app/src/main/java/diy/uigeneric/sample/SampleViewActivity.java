package diy.uigeneric.sample;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import diy.uigeneric.R;
import diy.uigeneric.data.Sample;
import diy.uigeneric.data.SampleDataSource;

public class SampleViewActivity extends AppCompatActivity {

    private static final String TAG = "SampleViewActivity";

    private static final int REQUEST_EDIT = 100;

    private static final int ICON_DEFAULT_DRAWABLE = R.drawable.ic_face_black_48dp;

    private ImageView imageIcon = null;
    private TextView textName = null;
    private TextView textCategory = null;
    private TextView textDetail = null;

    private Sample item = null;
    private boolean changed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        imageIcon = (ImageView) findViewById(R.id.image_icon);
        textName = (TextView) findViewById(R.id.text_name);
        textCategory = (TextView) findViewById(R.id.text_category);
        textDetail = (TextView) findViewById(R.id.text_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            SampleDataSource source = new SampleDataSource(this);
            source.open();
            long id = bundle.getLong("data.id");
            item = source.get(id);
            source.close();
        }
        uiFromData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            back();
            return true;
        }
        else if (id == R.id.action_edit) {
            edit();
            return true;
        }
        else if (id == R.id.action_delete) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            SampleDataSource source = new SampleDataSource(this);
            source.open();
            item = source.get(item.getId());
            source.close();
            uiFromData();
            changed = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //savedInstanceState.putLong("data.id", item.getId());
        savedInstanceState.putBoolean("data.changed", changed);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        changed = savedInstanceState.getBoolean("data.changed");
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        if (changed) {
            Intent resultIntent = new Intent();
            //resultIntent.putExtra("data.id", item.getId());
            resultIntent.putExtra("data.changed", changed);
            setResult(Activity.RESULT_OK, resultIntent);
        }
        finish();
    }

    private void edit() {
        Intent intent = new Intent(this, SampleEditActivity.class);
        intent.putExtra("data.id", item.getId());
        startActivityForResult(intent, REQUEST_EDIT);
    }

    private void delete() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to move this item to trash?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        resultIntent.putExtra("data.generic.id", fragment.getItemId());
                        resultIntent.putExtra("data.generic.deleted", true);
                        fragment.delete();
                        finish();
                        */
                    }
                })
                .show();
    }

    private void uiFromData() {
        if (item.getId() > 0) {
            if (item.getIcon() != null) {
                Drawable drawable = new BitmapDrawable(this.getResources(), item.getIcon());
                imageIcon.setImageDrawable(drawable);
            }
            else
                imageIcon.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_face_black_48dp));
        }
        else
            imageIcon.setImageDrawable(null);

        textName.setText(item.getName());
        textCategory.setText(Sample.categoryToString(this, item.getCategory()));

        Typeface typeface = Typeface.create("monospace", 0);
        textDetail.setTypeface(typeface);
        textDetail.setText(item.getDetail());
    }

    public long getItemId() {
        return item.getId();
    }

}

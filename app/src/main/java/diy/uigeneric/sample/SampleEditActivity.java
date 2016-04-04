package diy.uigeneric.sample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import diy.uigeneric.R;
import diy.uigeneric.data.Sample;
import diy.uigeneric.data.SampleDataSource;

public class SampleEditActivity extends AppCompatActivity {

    private static final String TAG = "SampleEditActivity";

    private static final int REQUEST_CODE_TAKE_PHOTO = 100;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 101;

    private ImageView imageIcon = null;
    private EditText editName = null;
    private Spinner spinCategory = null;
    private EditText editDetail = null;

    private PopupMenu menuIcon = null;
    private ArrayAdapter<CharSequence> arrayAdapter = null;

    private Sample item = null;
    private boolean iconChanged = false;
    private Uri tempUri = null;
    private int category = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageIcon = (ImageView) findViewById(R.id.image_icon);
        editName = (EditText) findViewById(R.id.edit_name);
        spinCategory = (Spinner) findViewById(R.id.spin_category);
        editDetail = (EditText) findViewById(R.id.edit_detail);

        Button buttonChange = (Button) findViewById(R.id.button_change);
        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuIcon.show();
            }
        });
        menuIcon = new PopupMenu(this, buttonChange);
        menuIcon.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_icon_take_photo) {
                    String external = Environment.getExternalStorageState();
                    if (external.equals(Environment.MEDIA_MOUNTED)) {
                        try {
                            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                            File temp = File.createTempFile("temp-", ".jpg", path);
                            tempUri = Uri.fromFile(temp);
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
                else if (id == R.id.action_icon_choose_photo) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, REQUEST_CODE_CHOOSE_PHOTO);
                    return true;
                }
                else if (id == R.id.action_icon_default_photo) {
                    iconChanged = true;
                    imageIcon.setImageBitmap(null);
                    return true;
                }
                return false;
            }
        });
        menuIcon.inflate(R.menu.sample_edit_icon);

        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.sample_edit_category, android.R.layout.simple_spinner_dropdown_item);
        spinCategory.setAdapter(arrayAdapter);
        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = -1;
            }
        });

        if (savedInstanceState != null) {
            long id = savedInstanceState.getLong("data.id");
            if (id > 0) {
                SampleDataSource source = new SampleDataSource(this);
                source.open();
                item = source.get(id);
                source.close();
            }
            iconChanged = savedInstanceState.getBoolean("data.icon_changed");
            imageIcon.setImageBitmap(null);
            if (savedInstanceState.containsKey("data.icon")) {
                imageIcon.setImageBitmap((Bitmap) savedInstanceState.getParcelable("data.icon"));
            }
        }
        else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                SampleDataSource source = new SampleDataSource(this);
                source.open();
                long id = bundle.getLong("data.id");
                item = source.get(id);
                source.close();
            }
            else {
                item = new Sample();
            }
            iconChanged = false;
            tempUri = null;
            category = 0;
        }

        uiFromData();
        Log.d(TAG, "onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            save();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream stream = getContentResolver().openInputStream(tempUri);
                    if (stream != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(stream);
                        stream.close();
                        imageIcon.setImageBitmap(bitmap);
                        iconChanged = true;
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                File file = new File(tempUri.getPath());
                file.delete();
            }
        }
        else if (requestCode == REQUEST_CODE_CHOOSE_PHOTO && resultCode == Activity.RESULT_OK) {
            try {
                InputStream stream = getContentResolver().openInputStream(resultIntent.getData());
                if (stream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    stream.close();
                    imageIcon.setImageBitmap(bitmap);
                    iconChanged = true;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("data.id", item.getId());
        savedInstanceState.putBoolean("data.icon_changed", iconChanged);
        if (imageIcon.getDrawable() != null && ((BitmapDrawable) imageIcon.getDrawable()).getBitmap() != null)
            savedInstanceState.putParcelable("data.icon", ((BitmapDrawable) imageIcon.getDrawable()).getBitmap());
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
    }

    private void uiFromData() {
        imageIcon.setImageDrawable(item.getIcon() != null ? new BitmapDrawable(getResources(), item.getIcon()) : null);
        editName.setText(item.getName());
        spinCategory.setSelection(item.getCategory());
        editDetail.setText(item.getDetail());
    }

    private void uiToData() {
        item.setIcon(imageIcon.getDrawable() != null ? ((BitmapDrawable) imageIcon.getDrawable()).getBitmap() : null);
        item.setName(editName.getText().toString().trim());
        item.setCategory(category);
        item.setDetail(editDetail.getText().toString());
    }

    private boolean empty() {
        return editName.getText().toString().trim().length() <= 0;
    }

    private boolean changed() {
        return iconChanged ||
                category != item.getCategory() ||
                !editName.getText().toString().trim().equals(item.getName()) ||
                !editDetail.getText().toString().equals(item.getDetail());
    }

    private void save() {
        if (!empty()) {
            if (changed()) {
                uiToData();

                SampleDataSource source = new SampleDataSource(this);
                source.open();
                if (item.getId() > 0) {
                    source.update(item);
                    Log.d(TAG, "saved " + item.getId() + "/" + item.getName());
                }
                else {
                    long id = source.insert(item);
                    item = source.get(id);
                    Log.d(TAG, "added " + item.getId() + "/" + item.getName());
                }
                source.close();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("data.id", item.getId());
                setResult(Activity.RESULT_OK, resultIntent);
            }
        }
    }

    public long getItemId() {
        return item.getId();
    }

}

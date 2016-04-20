package diy.uigeneric.sample;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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

/**
 * The SampleEditActivity is an activity to edit diy.uigeneric.data.sample class data.
 * <p/>
 * Starts the activity to add data.  If user want to edit data, pass bundle "data.id" as long
 * integer.
 * <p/>
 * There are two close events.  First, back pressed, just nothings change.  Second, when Save
 * button saved and data can be saved.
 */
public class SampleEditActivity extends AppCompatActivity {

    private static final String TAG = SampleEditActivity.class.getSimpleName();

    private static final int REQUEST_CODE_TAKE_PHOTO = 100;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 101;

    private static final int ICON_DEFAULT_DRAWABLE = R.drawable.ic_face_black_48dp;

    private ImageView imageIcon = null;
    private EditText editName = null;
    private Spinner spinCategory = null;
    private EditText editDetail = null;

    private PopupMenu menuIcon = null;

    private Sample item = null;
    private boolean iconChanged = false;
    private boolean iconDefault = false;
    private Uri iconTempUrl = null;
    private int categorySelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        imageIcon = (ImageView) findViewById(R.id.image_icon);
        editName = (EditText) findViewById(R.id.edit_name);
        spinCategory = (Spinner) findViewById(R.id.spin_category);
        editDetail = (EditText) findViewById(R.id.edit_detail);

        Button buttonChange = (Button) findViewById(R.id.button_change);
        if (buttonChange != null) {
            buttonChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    menuIcon.show();
                }
            });
        }
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
                            File temp = File.createTempFile("uigeneric-", ".jpg", path);
                            iconTempUrl = Uri.fromFile(temp);
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, iconTempUrl);
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
                    iconDefault = true;
                    imageIcon.setImageResource(ICON_DEFAULT_DRAWABLE);
                    return true;
                }
                return false;
            }
        });
        menuIcon.inflate(R.menu.sample_edit_icon);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.sample_category, android.R.layout.simple_spinner_item);
        spinCategory.setAdapter(arrayAdapter);
        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorySelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categorySelected = -1;
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            SampleDataSource source = new SampleDataSource(this);
            source.open();
            long id = bundle.getLong("data.id");
            item = source.get(id);
            source.close();
            categorySelected = item.getCategory();
            if (actionBar != null)
                actionBar.setTitle(R.string.sample_edit_title_edit);
        }
        else {
            item = new Sample();
            categorySelected = 0;
            if (actionBar != null)
                actionBar.setTitle(R.string.sample_edit_title_add);
        }
        iconChanged = false;
        iconDefault = item.getIcon() == null;
        iconTempUrl = null;
        uiFromData();
        Log.d(TAG, "onCreate() item: " + item.getId() + "/" + item.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            back();
            return true;
        }
        else if (id == R.id.action_save) {
            if (save()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("data.id", this.item.getId());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
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
                    InputStream stream = getContentResolver().openInputStream(iconTempUrl);
                    if (stream != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(stream);
                        stream.close();
                        imageIcon.setImageBitmap(bitmap);
                        iconChanged = true;
                        iconDefault = false;
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                File file = new File(iconTempUrl.getPath());
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
                    iconDefault = false;
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
        savedInstanceState.putBoolean("data.iconChanged", iconChanged);
        savedInstanceState.putBoolean("data.iconDefault", iconDefault);
        savedInstanceState.putParcelable("data.iconTempUrl", iconTempUrl);
        if (!iconDefault)
            savedInstanceState.putParcelable("data.icon", ((BitmapDrawable) imageIcon.getDrawable()).getBitmap());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        iconChanged = savedInstanceState.getBoolean("data.iconChanged");
        iconDefault = savedInstanceState.getBoolean("data.iconDefault");
        iconTempUrl = savedInstanceState.getParcelable("data.iconTempUrl");
        if (!iconDefault)
            imageIcon.setImageBitmap((Bitmap) savedInstanceState.getParcelable("data.icon"));
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        if (changed()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.sample_edit_dialog_title)
                    .setMessage(R.string.sample_edit_dialog_message)
                    .setNegativeButton(R.string.sample_edit_dialog_negative, null)
                    .setPositiveButton(R.string.sample_edit_dialog_positive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .show();
        }
        else
            finish();
    }

    private boolean save() {
        if (!empty() && changed()) {
            uiToData();

            SampleDataSource source = new SampleDataSource(this);
            source.open();
            if (item.getId() > 0) {
                source.update(item);
                Log.d(TAG, "saved item: " + item.getId() + "/" + item.getName());
            }
            else {
                long id = source.insert(item);
                item = source.get(id);
                Log.d(TAG, "added item: " + item.getId() + "/" + item.getName());
            }
            source.close();

            iconChanged = false;
            return true;
        }
        return false;
    }

    private boolean empty() {
        return editName.getText().toString().trim().length() <= 0;
    }

    private boolean changed() {
        return iconChanged ||
                categorySelected != item.getCategory() ||
                !editName.getText().toString().trim().equals(item.getName()) ||
                !editDetail.getText().toString().equals(item.getDetail());
    }

    private void uiFromData() {
        if (!iconDefault)
            imageIcon.setImageDrawable(new BitmapDrawable(getResources(), item.getIcon()));
        else
            imageIcon.setImageResource(ICON_DEFAULT_DRAWABLE);
        editName.setText(item.getName());
        spinCategory.setSelection(item.getCategory());
        editDetail.setText(item.getDetail());
    }

    private void uiToData() {
        item.setIcon(!iconDefault ? ((BitmapDrawable) imageIcon.getDrawable()).getBitmap() : null);
        item.setName(editName.getText().toString().trim());
        item.setCategory(categorySelected);
        item.setDetail(editDetail.getText().toString());
    }

    public long getItemId() {
        return item.getId();
    }

}

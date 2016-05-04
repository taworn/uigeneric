package diy.uigeneric.sampleserver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import diy.restlite.HttpRestLite;
import diy.uigeneric.R;
import diy.uigeneric.data.Sample;
import diy.uigeneric.data.SampleServerDataSource;

/**
 * The SampleEditActivity is an activity to edit a Sample data.
 */
public class SampleServerEditActivity extends AppCompatActivity {

    private static final String TAG = SampleServerEditActivity.class.getSimpleName();

    private static final int REQUEST_CODE_TAKE_PHOTO = 100;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 101;

    private ProgressDialog progress = null;
    private DialogInterface.OnCancelListener progressCancel = null;
    private HttpRestLite rest = null;
    private HttpRestLite.ResultListener listener = null;

    private ImageView imageIcon = null;
    private EditText editName = null;
    private Spinner spinCategory = null;
    private EditText editDetail = null;

    private PopupMenu menuIcon = null;

    private SampleServerDataSource source = null;
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

        // initializes callback
        listener = new HttpRestLite.ResultListener() {
            @Override
            public void finish(final HttpRestLite.Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        commonResultTask(result);
                    }
                });
            }
        };
        progressCancel = new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (rest != null) {
                    rest.cancel();
                }
            }
        };

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
                    imageIcon.setImageResource(R.drawable.ic_face_black_48dp);
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

        source = new SampleServerDataSource(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            long id = bundle.getLong("data.id");
            item = new Sample();
            categorySelected = 0;
            openProgressDialog();
            source.get(id, new SampleServerDataSource.ResultListener() {
                @Override
                public void finish(final HttpRestLite.Result result, @NonNull final SampleServerDataSource.SampleHolder holder) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            commonResultTask(result);
                            if (result.errorCode == 0) {
                                item = holder.sample;
                                categorySelected = item.getCategory();
                                iconChanged = false;
                                iconDefault = item.getIcon() == null;
                                iconTempUrl = null;
                                uiFromData();
                                Log.d(TAG, "load item: " + item.getId() + "/" + item.getName());
                            }
                            else
                                SampleServerEditActivity.this.finish();
                        }
                    });
                }
            });
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
    protected void onPause() {
        super.onPause();
        if (rest != null)
            rest.cancel();
        if (progress != null)
            progress.cancel();
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
                    .setTitle(R.string.sample_discard_dialog_title)
                    .setMessage(R.string.sample_discard_dialog_message)
                    .setNegativeButton(R.string.sample_discard_dialog_negative, null)
                    .setPositiveButton(R.string.sample_discard_dialog_positive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .show();
        }
        else
            finish();
    }

    private void save() {
        if (!empty() && changed()) {
            uiToData();

            /*
            if (item.getId() > 0) {
                source.update(item);
                Log.d(TAG, "saved item: " + item.getId() + "/" + item.getName());
            }
            else {
                long id = source.insert(item);
                item = source.get(id);
                Log.d(TAG, "added item: " + item.getId() + "/" + item.getName());
            }
            */
            openProgressDialog();
            source.add(item, new SampleServerDataSource.ResultListener() {
                @Override
                public void finish(final HttpRestLite.Result result, @NonNull final SampleServerDataSource.SampleHolder holder) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            commonResultTask(result);
                            if (result.errorCode == 0) {
                                Log.d(TAG, "added item: " + item.getId() + "/" + item.getName());
                                iconChanged = false;
                                item = holder.sample;

                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("data.id", item.getId());
                                setResult(Activity.RESULT_OK, resultIntent);
                                SampleServerEditActivity.this.finish();
                            }
                        }
                    });
                }
            });
        }
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
            imageIcon.setImageResource(R.drawable.ic_face_black_48dp);
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

    private void openProgressDialog() {
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage(getResources().getString(R.string.sample_waiting));
        progress.setIndeterminate(true);
        progress.setCancelable(true);
        progress.setCanceledOnTouchOutside(true);
        progress.setOnCancelListener(progressCancel);
        progress.show();
    }

    private void commonResultTask(HttpRestLite.Result result) {
        progress.dismiss();
        if (result.errorCode == HttpRestLite.ERROR_CUSTOM) {
            if (result.json.has("errors")) {
                try {
                    JSONArray errors = result.json.getJSONArray("errors");
                    String message = "";
                    for (int i = 0; i < errors.length(); i++)
                        message += " - " + errors.get(i) + "\n";
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.sample_error_title)
                            .setMessage(message)
                            .setNeutralButton(R.string.sample_error_neutral_button, null)
                            .show();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, R.string.sample_error_unknown_json, Toast.LENGTH_SHORT).show();
                }
            }
            else
                Toast.makeText(this, R.string.sample_error_unknown_json, Toast.LENGTH_SHORT).show();
        }
        else if (result.errorCode != 0) {
            Toast.makeText(this, HttpRestLite.getErrorMessage(this, result.errorCode), Toast.LENGTH_SHORT).show();
        }
    }

}

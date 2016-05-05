package diy.uigeneric.sampleserver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import diy.restlite.HttpRestLite;
import diy.uigeneric.R;
import diy.uigeneric.data.Sample;
import diy.uigeneric.data.SampleServerDataSource;
import diy.uigeneric.data.SampleServerIndirectList;

/**
 * The SampleServerViewActivity is an activity to view a Sample data.
 */
public class SampleServerViewActivity extends AppCompatActivity {

    private static final String TAG = SampleServerViewActivity.class.getSimpleName();

    private static final int REQUEST_EDIT = 100;

    private ProgressDialog progress = null;
    private DialogInterface.OnCancelListener progressCancel = null;
    private HttpRestLite.ResultListener listener = null;
    private boolean loading = false;

    private ImageView imageIcon = null;
    private TextView textName = null;
    private TextView textCategory = null;
    private TextView textDetail = null;

    private SampleServerDataSource source = null;
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

        progressCancel = new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                source.cancel();
            }
        };
        listener = new HttpRestLite.ResultListener() {
            @Override
            public void finish(HttpRestLite.Result result) {
                commonResultTask(result);
            }
        };

        imageIcon = (ImageView) findViewById(R.id.image_icon);
        textName = (TextView) findViewById(R.id.text_name);
        textCategory = (TextView) findViewById(R.id.text_category);
        textDetail = (TextView) findViewById(R.id.text_detail);

        source = new SampleServerDataSource(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            long id = bundle.getLong("data.id");
            item = new Sample();
            openProgressDialog();
            source.get(id, new SampleServerDataSource.ResultListener() {
                @Override
                public void finish(HttpRestLite.Result result, @NonNull SampleServerDataSource.SampleHolder holder) {
                    commonResultTask(result);
                    if (result.errorCode == 0) {
                        item = holder.sample;
                        uiFromData();
                        invalidateOptionsMenu();
                        Log.d(TAG, "onCreate() item: " + item.getId() + "/" + item.getName());
                    }
                }
            });
            if (actionBar != null)
                actionBar.setTitle(item.getDeleted() == null ? R.string.sample_view_title : R.string.sample_view_title_deleted);
        }
        else {
            Log.e(TAG, "BUG:");
            Log.e(TAG, "This line is NOT should be called.");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        source.cancel();
        if (progress != null)
            progress.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_view, menu);
        if (item.getDeleted() == null) {
            menu.findItem(R.id.action_restore).setVisible(false);
            menu.findItem(R.id.action_remove).setVisible(false);
        }
        else {
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(false);
        }
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
        else if (id == R.id.action_restore) {
            restore();
            return true;
        }
        else if (id == R.id.action_delete) {
            delete();
            return true;
        }
        else if (id == R.id.action_remove) {
            remove();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult(), return from SampleServerEditActivity");
            if (!loading) {
                changed = true;
                openProgressDialog();
                source.get(item.getId(), new SampleServerDataSource.ResultListener() {
                    @Override
                    public void finish(HttpRestLite.Result result, @NonNull SampleServerDataSource.SampleHolder holder) {
                        commonResultTask(result);
                        if (result.errorCode == 0) {
                            item = holder.sample;
                            uiFromData();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
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
            resultIntent.putExtra("data.id", item.getId());
            resultIntent.putExtra("data.changed", true);
            setResult(Activity.RESULT_OK, resultIntent);
            Log.d(TAG, "back(), item: " + item.getId() + "/" + item.getName());
        }
        finish();
    }

    private void edit() {
        Log.d(TAG, "edit(), call SampleServerEditActivity, item: " + item.getId() + "/" + item.getName());
        Intent intent = new Intent(this, SampleServerEditActivity.class);
        intent.putExtra("data.id", item.getId());
        startActivityForResult(intent, REQUEST_EDIT);
    }

    private void restore() {
        final List<Long> idList = new ArrayList<>();
        idList.add(item.getId());
        openProgressDialog();
        SampleServerIndirectList list = new SampleServerIndirectList(this);
        list.restore(idList, new HttpRestLite.ResultListener() {
            @Override
            public void finish(HttpRestLite.Result result) {
                commonResultTask(result);
                if (result.errorCode == 0) {
                    Log.d(TAG, "restored item: " + item.getId() + "/" + item.getName());
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("data.id", item.getId());
                    resultIntent.putExtra("data.deleted", true);
                    setResult(Activity.RESULT_OK, resultIntent);
                    SampleServerViewActivity.this.finish();
                }
            }
        });
    }

    private void delete() {
        final List<Long> idList = new ArrayList<>();
        idList.add(item.getId());
        openProgressDialog();
        SampleServerIndirectList list = new SampleServerIndirectList(this);
        list.delete(idList, new HttpRestLite.ResultListener() {
            @Override
            public void finish(HttpRestLite.Result result) {
                commonResultTask(result);
                if (result.errorCode == 0) {
                    Log.d(TAG, "deleted item: " + item.getId() + "/" + item.getName());
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("data.id", item.getId());
                    resultIntent.putExtra("data.deleted", true);
                    setResult(Activity.RESULT_OK, resultIntent);
                    SampleServerViewActivity.this.finish();
                }
            }
        });
    }

    private void remove() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.sample_remove_dialog_title)
                .setMessage(R.string.sample_remove_dialog_message)
                .setNegativeButton(R.string.sample_remove_dialog_negative, null)
                .setPositiveButton(R.string.sample_remove_dialog_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final List<Long> idList = new ArrayList<>();
                        idList.add(item.getId());
                        openProgressDialog();
                        SampleServerIndirectList list = new SampleServerIndirectList(SampleServerViewActivity.this);
                        list.remove(idList, new HttpRestLite.ResultListener() {
                            @Override
                            public void finish(HttpRestLite.Result result) {
                                commonResultTask(result);
                                if (result.errorCode == 0) {
                                    Log.d(TAG, "removed item: " + item.getId() + "/" + item.getName());
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("data.id", item.getId());
                                    resultIntent.putExtra("data.deleted", true);
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    SampleServerViewActivity.this.finish();
                                }
                            }
                        });
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

        String[] categoryList = getResources().getStringArray(R.array.sample_category);
        String categoryName = Integer.toString(item.getCategory());
        if (item.getCategory() >= 0 && item.getCategory() < categoryList.length)
            categoryName = categoryList[item.getCategory()];
        textCategory.setText(categoryName);

        Typeface typeface = Typeface.create("monospace", 0);
        textDetail.setTypeface(typeface);
        textDetail.setText(item.getDetail());
    }

    public long getItemId() {
        return item.getId();
    }

    private void openProgressDialog() {
        loading = true;
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
        loading = false;
        if (result.errorCode == HttpRestLite.ERROR_CANCEL) {
            Log.d(TAG, "user cancelled HTTP REST");
        }
        else if (result.errorCode != 0) {
            String errorMessage;
            if (result.errorCode == HttpRestLite.ERROR_CUSTOM) {
                if (result.json.has("errors")) {
                    try {
                        JSONArray errors = result.json.getJSONArray("errors");
                        errorMessage = "";
                        for (int i = 0; i < errors.length(); i++)
                            errorMessage += " - " + errors.get(i) + "\n";
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        errorMessage = getResources().getString(R.string.sample_error_unknown_json);
                    }
                }
                else
                    errorMessage = getResources().getString(R.string.sample_error_unknown_json);
            }
            else
                errorMessage = HttpRestLite.getErrorMessage(this, result.errorCode);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.sample_error_title)
                    .setMessage(errorMessage)
                    .setNeutralButton(R.string.sample_error_neutral_button, null)
                    .show();
        }
    }

}

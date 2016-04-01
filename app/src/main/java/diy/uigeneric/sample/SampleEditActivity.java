package diy.uigeneric.sample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
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

import java.io.IOException;
import java.io.InputStream;

import diy.uigeneric.R;
import diy.uigeneric.data.Sample;

public class SampleEditActivity extends AppCompatActivity {

    private static final String TAG = "SampleEditActivity";

    private static final int PHOTO_REQUEST_CODE = 100;

    private ImageView imageIcon = null;
    private EditText editName = null;
    private Spinner spinCategory = null;
    private EditText editDetail = null;

    private PopupMenu menuIcon = null;
    private ArrayAdapter<CharSequence> arrayAdapter = null;

    private Sample item = null;
    private boolean iconChanged = false;
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
                    Toast.makeText(SampleEditActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (id == R.id.action_icon_choose_photo) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, PHOTO_REQUEST_CODE);
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

        item = new Sample();
        iconChanged = false;
        category = 0;

        uiFromData();
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
            Toast.makeText(this, "Not implement", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
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

}

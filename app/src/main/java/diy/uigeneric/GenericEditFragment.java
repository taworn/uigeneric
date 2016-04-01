package diy.uigeneric;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.IOException;
import java.io.InputStream;

/**
 * This fragment is simplify way to edit a Generic object.
 */
public class GenericEditFragment extends Fragment {

    private static final String TAG = "GenericEditFragment";

    private static final int PICTURE_REQUEST_CODE = 100;

    private Generic item = null;
    private boolean iconChanged = false;
    private int category = 0;

    private ImageView imageIcon = null;
    private EditText editName = null;
    private Spinner spinCategory = null;
    private EditText editDetail = null;

    public GenericEditFragment() {
        // Required empty public constructor
    }

    public static GenericEditFragment newInstance() {
        GenericEditFragment fragment = new GenericEditFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = new Generic();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.generic_fragment_edit, container, false);

        imageIcon = (ImageView) view.findViewById(R.id.image_icon);
        editName = (EditText) view.findViewById(R.id.edit_name);
        spinCategory = (Spinner) view.findViewById(R.id.spin_category);
        editDetail = (EditText) view.findViewById(R.id.edit_detail);

        Button buttonChoose = (Button) view.findViewById(R.id.button_choose);
        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, PICTURE_REQUEST_CODE);
            }
        });

        Button buttonClear = (Button) view.findViewById(R.id.button_clear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iconChanged = true;
                imageIcon.setImageBitmap(null);
            }
        });

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, Generic.categoryToString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCategory.setAdapter(adapter);
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

        uiFromData();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (requestCode == PICTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            try {
                InputStream stream = getActivity().getContentResolver().openInputStream(resultIntent.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
                imageIcon.setImageBitmap(bitmap);
                iconChanged = true;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            long id = savedInstanceState.getLong("generic.id");
            if (id > 0) {
                GenericDataSource source = new GenericDataSource(getActivity());
                source.open();
                item = source.get(id);
                source.close();
            }
            iconChanged = savedInstanceState.getBoolean("generic.icon_changed");
            imageIcon.setImageBitmap(null);
            if (savedInstanceState.containsKey("generic.icon")) {
                imageIcon.setImageBitmap((Bitmap) savedInstanceState.getParcelable("generic.icon"));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("generic.id", item.getId());
        savedInstanceState.putBoolean("generic.icon_changed", iconChanged);
        if (imageIcon.getDrawable() != null && ((BitmapDrawable) imageIcon.getDrawable()).getBitmap() != null)
            savedInstanceState.putParcelable("generic.icon", ((BitmapDrawable) imageIcon.getDrawable()).getBitmap());
    }

    /**
     * @return Returns an object.
     */
    public Generic getItem() {
        return item;
    }

    /**
     * @return Returns an object id.
     */
    public long getItemId() {
        return item.getId();
    }

    /**
     * Changes an object to the new data.
     *
     * @param id The id to a new object.
     */
    public void setItemId(long id) {
        if (id > 0) {
            GenericDataSource source = new GenericDataSource(getActivity());
            source.open();
            item = source.get(id);
            source.close();
        }
        else
            item = new Generic();
        uiFromData();
    }

    /**
     * Saves an object.
     *
     * @return Returns true if it successful.  Otherwise, it returns false.
     */
    public boolean save() {
        if (empty())
            return false;
        if (!changed())
            return true;
        uiToData();

        GenericDataSource source = new GenericDataSource(getActivity());
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

        return true;
    }

    /**
     * Checks this object has changed.
     *
     * @return Returns true if it changed.
     */
    public boolean changed() {
        return iconChanged ||
                category != item.getCategory() ||
                !editName.getText().toString().trim().equals(item.getName()) ||
                !editDetail.getText().toString().equals(item.getDetail());
    }

    /**
     * Checks this object has empty state.
     *
     * @return Returns true if it emptied.
     */
    public boolean empty() {
        return editName.getText().toString().trim().length() <= 0;
    }

    private void uiFromData() {
        if (item.getIcon() != null) {
            Drawable drawable = new BitmapDrawable(getActivity().getResources(), item.getIcon());
            imageIcon.setImageDrawable(drawable);
        }
        else
            imageIcon.setImageDrawable(null);
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

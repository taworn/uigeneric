package diy.uigeneric;

import android.app.Fragment;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This fragment is simplify way to view a Generic object.
 */
public class GenericViewFragment extends Fragment {

    private static final String TAG = "GenericViewFragment";

    private Generic item = null;

    private ImageView imageIcon = null;
    private TextView textName = null;
    private TextView textCategory = null;
    private TextView textDetail = null;

    public GenericViewFragment() {
        // Required empty public constructor
    }

    public static GenericViewFragment newInstance() {
        GenericViewFragment fragment = new GenericViewFragment();
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
        View view = inflater.inflate(R.layout.generic_fragment_view, container, false);

        imageIcon = (ImageView) view.findViewById(R.id.image_icon);
        textName = (TextView) view.findViewById(R.id.text_name);
        textCategory = (TextView) view.findViewById(R.id.text_category);
        textDetail = (TextView) view.findViewById(R.id.text_detail);

        uiFromData();
        return view;
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
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("generic.id", item.getId());
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
     * Reloads an object.
     */
    public void reload() {
        setItemId(item.getId());
    }

    /**
     * Restores an object.
     */
    public void restore() {
        if (item.getId() > 0) {
            GenericDataSource source = new GenericDataSource(getActivity());
            source.open();
            source.restore(item.getId());
            source.close();
            Log.d(TAG, "restored " + item.getId() + "/" + item.getName());
        }
        reload();
    }

    /**
     * Deletes an object.  It moves data to trash.
     */
    public void delete() {
        if (item.getId() > 0) {
            GenericDataSource source = new GenericDataSource(getActivity());
            source.open();
            source.delete(item.getId());
            source.close();
            Log.d(TAG, "deleted " + item.getId() + "/" + item.getName());
        }
        setItemId(0);
    }

    /**
     * Deletes an object.  It don't stores data in trash.
     */
    public void deleteReal() {
        if (item.getId() > 0) {
            GenericDataSource source = new GenericDataSource(getActivity());
            source.open();
            source.deleteReal(item.getId());
            source.close();
            Log.d(TAG, "deleted (no trash) " + item.getId() + "/" + item.getName());
        }
        setItemId(0);
    }

    private void uiFromData() {
        if (item.getId() > 0) {
            if (item.getIcon() != null) {
                Drawable drawable = new BitmapDrawable(this.getResources(), item.getIcon());
                imageIcon.setImageDrawable(drawable);
            }
            else
                imageIcon.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_launcher));
        }
        else
            imageIcon.setImageDrawable(null);

        textName.setText(item.getName());
        textCategory.setText(Generic.categoryToString[item.getCategory()]);

        Typeface typeface = Typeface.create("monospace", 0);
        textDetail.setTypeface(typeface);
        textDetail.setText(item.getDetail());
    }

}

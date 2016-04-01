package diy.uigeneric;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

/**
 * This fragment is simplify way to list a Generic object.
 */
public class GenericListFragment extends Fragment {

    private static final String TAG = "GenericListFragment";

    private Listener listener = null;

    private Boolean deleted = false;
    private Integer category = Generic.CATEGORY_DATA;
    private String query = null;
    private int sortBy = GenericListIndirect.SORT_AS_IS;
    private boolean sortReverse = false;

    private ListView listView = null;
    private GenericListAdapter adapter = null;
    private GenericListIndirect list = null;
    private ActionMode actionMode = null;

    public GenericListFragment() {
        // Required empty public constructor
    }

    public static GenericListFragment newInstance() {
        GenericListFragment fragment = new GenericListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.generic_fragment_list, container, false);
        listView = (ListView) view.findViewById(R.id.list_view);

        list = new GenericListIndirect();
        adapter = new GenericListAdapter(getActivity(), list, deleted);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (listener == null || listener.isCreateCAB()) {
                    actionMode = mode;
                    adapter.toggleSelection(position);
                    adapter.notifyDataSetChanged();
                    actionMode.setTitle(adapter.getSelectedCount() + " item(s)");
                }
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.action_restore:
                        restoreSelected();
                        return true;

                    case R.id.action_delete:
                        if (listener != null && listener.isConfirmDeleteSelected())
                            listener.confirmDeleteSelected();
                        else
                            deleteSelected();
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                if (listener == null || listener.isCreateCAB()) {
                    // Inflate the menu for the CAB
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.generic_menu_context, menu);
                    if (deleted) {
                        MenuItem item = menu.findItem(R.id.action_restore);
                        item.setVisible(true);
                    }
                    return true;
                }
                else
                    return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
                adapter.removeSelection();
                actionMode = null;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (actionMode == null) {
                    GenericListIndirect.Item item = (GenericListIndirect.Item) adapter.getItem(i);
                    if (listener != null)
                        listener.onItem(item.object.getId());
                }
                else {
                    adapter.toggleSelection(i);
                    adapter.notifyDataSetChanged();
                    actionMode.setTitle(adapter.getSelectedCount() + " item(s)");
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("generic.has_deleted"))
                deleted = savedInstanceState.getBoolean("generic.deleted");
            else
                deleted = null;

            if (savedInstanceState.getBoolean("generic.has_category"))
                category = savedInstanceState.getInt("generic.category");
            else
                category = null;

            if (savedInstanceState.getBoolean("generic.has_query"))
                query = savedInstanceState.getString("generic.query");
            else
                query = null;

            sortBy = savedInstanceState.getInt("generic.sort_by");
            sortReverse = savedInstanceState.getBoolean("generic.sort_reverse");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("generic.has_deleted", deleted != null);
        if (deleted != null)
            savedInstanceState.putBoolean("generic.deleted", deleted);

        savedInstanceState.putBoolean("generic.has_category", category != null);
        if (category != null)
            savedInstanceState.putInt("generic.category", category);

        savedInstanceState.putBoolean("generic.has_query", query != null);
        if (query != null)
            savedInstanceState.putString("generic.query", query);

        savedInstanceState.putInt("generic.sort_by", sortBy);
        savedInstanceState.putBoolean("generic.sort_reverse", sortReverse);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (actionMode != null) {
            actionMode.finish();
            actionMode = null;
        }
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener value) {
        listener = value;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean value) {
        deleted = value;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer value) {
        category = value;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String value) {
        query = value;
    }

    public int getSortBy() {
        return sortBy;
    }

    public void setSortBy(int value) {
        sortBy = value;
    }

    public boolean getSortReverse() {
        return sortReverse;
    }

    public void setSortReverse(boolean value) {
        sortReverse = value;
    }

    public int getCount() {
        return adapter.getCount();
    }

    public int getCount(Boolean deleted, Integer category, String query) {
        GenericDataSource source = new GenericDataSource(getActivity());
        source.open();
        int count = source.count(deleted, category, query);
        source.close();
        return count;
    }

    public Generic getItem(int i) {
        return ((GenericListIndirect.Item) adapter.getItem(i)).object;
    }

    public void sort(int sortBy, boolean sortReverse) {
        this.sortBy = sortBy;
        this.sortReverse = sortReverse;
        list.sort(sortBy, sortReverse);
        adapter.notifyDataSetChanged();
    }

    public void load(Boolean deleted, Integer category, String query, int sortBy, boolean sortReverse) {
        this.deleted = deleted;
        this.category = category;
        this.query = query;
        this.sortBy = sortBy;
        this.sortReverse = sortReverse;
        list.load(getActivity(), deleted, category, query, sortBy, sortReverse);
        adapter.changeLayout(this.deleted != null ? this.deleted : false);
        adapter.notifyDataSetChanged();
        if (listener != null)
            listener.onCount(getCount());
    }

    public void load(Boolean deleted, Integer category, String query) {
        this.deleted = deleted;
        this.category = category;
        this.query = query;
        list.load(getActivity(), deleted, category, query, sortBy, sortReverse);
        adapter.changeLayout(this.deleted != null ? this.deleted : false);
        adapter.notifyDataSetChanged();
        if (listener != null)
            listener.onCount(getCount());
    }

    public void load(String query) {
        this.query = query;
        list.load(getActivity(), deleted, category, query, sortBy, sortReverse);
        adapter.changeLayout(this.deleted != null ? this.deleted : false);
        adapter.notifyDataSetChanged();
        if (listener != null)
            listener.onCount(getCount());
    }

    public void load() {
        list.load(getActivity(), deleted, category, query, sortBy, sortReverse);
        adapter.changeLayout(this.deleted != null ? this.deleted : false);
        adapter.notifyDataSetChanged();
        if (listener != null)
            listener.onCount(getCount());
    }

    public void restoreSelected() {
        if (adapter.getSelectedCount() > 0) {
            List<Long> list = adapter.getSelectedItems();
            if (list.size() > 0) {
                GenericDataSource source = new GenericDataSource(getActivity());
                source.open();
                source.restoreList(list);
                Log.d(TAG, "restored " + list.size() + " item(s)");
                source.close();
                load();
            }
            actionMode.finish();
        }
    }

    public List<Long> deleteSelected() {
        List<Long> list = null;
        if (adapter.getSelectedCount() > 0) {
            list = adapter.getSelectedItems();
            if (list.size() > 0) {
                GenericDataSource source = new GenericDataSource(getActivity());
                source.open();
                source.deleteList(list);
                Log.d(TAG, "deleted " + list.size() + " item(s)");
                source.close();
                load();
            }
            actionMode.finish();
        }
        return list;
    }

    public void deleteSelectedReal() {
        if (adapter.getSelectedCount() > 0) {
            List<Long> list = adapter.getSelectedItems();
            if (list.size() > 0) {
                GenericDataSource source = new GenericDataSource(getActivity());
                source.open();
                source.deleteListReal(list);
                Log.d(TAG, "deleted (no trash) " + list.size() + " item(s)");
                source.close();
                load();
            }
            actionMode.finish();
        }
    }

    public void deleteAll() {
        GenericDataSource source = new GenericDataSource(getActivity());
        source.open();
        source.deleteAll();
        Log.d(TAG, "deleted ALL item(s), no trash");
        source.close();
        load();
    }

    /**
     * The listener interface for callback.
     */
    public interface Listener {

        /**
         * Calls when data count changed.
         */
        void onCount(int count);

        /**
         * Calls when an item clicked.
         */
        void onItem(long id);

        /**
         * Calls when a delete selected is confirm.
         */
        void confirmDeleteSelected();

        /**
         * Calls when a confirmation message is needed or not.
         */
        boolean isConfirmDeleteSelected();

        /**
         * Calls when CAB is needed.
         */
        boolean isCreateCAB();

    }

}

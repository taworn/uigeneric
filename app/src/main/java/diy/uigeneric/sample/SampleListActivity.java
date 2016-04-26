package diy.uigeneric.sample;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;
import java.util.Locale;

import diy.uigeneric.R;
import diy.uigeneric.adapter.SampleListAdapter;
import diy.uigeneric.data.Sample;
import diy.uigeneric.data.SampleDataSource;
import diy.uigeneric.data.SampleIndirectList;

public class SampleListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = SampleListActivity.class.getSimpleName();

    private static final int REQUEST_ADD = 100;
    private static final int REQUEST_VIEW = 101;

    private ActionBar actionBar = null;
    private DrawerLayout drawer = null;
    private ActionBarDrawerToggle toggle = null;
    private SampleIndirectList list = null;
    private SampleListAdapter listAdapter = null;
    private RecyclerView listView = null;
    private boolean actionMode = false;
    private String saveTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        // initializes FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SampleListActivity.this, SampleEditActivity.class);
                    startActivityForResult(intent, REQUEST_ADD);
                }
            });
        }

        // initializes navigation drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null)
            navigationView.setNavigationItemSelectedListener(this);

        // initializes RecycledView and its data
        actionBar.setTitle(R.string.sample_list_title_data);
        list = new SampleIndirectList();
        list.load(this, false, Sample.CATEGORY_DATA, null, SampleIndirectList.SORT_AS_IS, false);
        listAdapter = new SampleListAdapter(this, list, new SampleListAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Sample item = list.get(position);
                Intent intent = new Intent(SampleListActivity.this, SampleViewActivity.class);
                intent.putExtra("data.id", item.getId());
                startActivityForResult(intent, REQUEST_VIEW);
            }

            @Override
            public void onLongClick(View view, int position) {
                listAdapter.toggleSelection(position);
                if (listAdapter.getSelected(position)) {
                    increaseListSelection();
                }
                else {
                    decreaseListSelection();
                }
            }
        });
        listView = (RecyclerView) findViewById(R.id.list_view);
        if (listView != null) {
            listView.setHasFixedSize(true);
            listView.setLayoutManager(new LinearLayoutManager(this));
            listView.setItemAnimator(new DefaultItemAnimator());
            listView.setAdapter(listAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sample_list, menu);

        // initializes radio and check menu items
        MenuItem item;
        switch (list.getSortBy()) {
            default:
            case SampleIndirectList.SORT_AS_IS:
                item = menu.findItem(R.id.action_sort_as_is);
                break;
            case SampleIndirectList.SORT_NAME:
                item = menu.findItem(R.id.action_sort_name);
                break;
            case SampleIndirectList.SORT_NAME_IGNORE_CASE:
                item = menu.findItem(R.id.action_sort_name_ignore_case);
                break;
            case SampleIndirectList.SORT_NAME_NATURAL:
                item = menu.findItem(R.id.action_sort_name_natural);
                break;
        }
        item.setChecked(true);
        item = menu.findItem(R.id.action_sort_reverse);
        item.setChecked(list.getSortReverse());

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    list.search(SampleListActivity.this, "");
                    listAdapter.notifyDataSetChanged();
                    Log.d(TAG, "search stop");
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_restore).setVisible(actionMode);
        menu.findItem(R.id.action_delete).setVisible(actionMode);
        menu.findItem(R.id.action_remove).setVisible(actionMode);
        menu.findItem(R.id.action_search).setVisible(!actionMode);
        menu.findItem(R.id.action_sort).setVisible(!actionMode);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            deleteSelected();
            return true;
        }
        else if (id == R.id.action_restore) {
            restoreSelected();
            return true;
        }
        else if (id == R.id.action_remove) {
            removeSelected();
            return true;
        }

        else if (id == R.id.action_sort_as_is) {
            item.setChecked(true);
            list.setSortBy(SampleIndirectList.SORT_AS_IS);
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "sort: as is");
            return true;
        }
        else if (id == R.id.action_sort_name) {
            item.setChecked(true);
            list.setSortBy(SampleIndirectList.SORT_NAME);
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "sort: name");
            return true;
        }
        else if (id == R.id.action_sort_name_ignore_case) {
            item.setChecked(true);
            list.setSortBy(SampleIndirectList.SORT_NAME_IGNORE_CASE);
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "sort: name ignore case");
            return true;
        }
        else if (id == R.id.action_sort_name_natural) {
            item.setChecked(true);
            list.setSortBy(SampleIndirectList.SORT_NAME_NATURAL);
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "sort: name natural");
            return true;
        }
        else if (id == R.id.action_sort_reverse) {
            item.setChecked(!item.isChecked());
            list.setSortReverse(!list.getSortReverse());
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "sort: " + (!list.getSortReverse() ? "ascending" : "descending"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_data) {
            actionBar.setTitle(R.string.sample_list_title_data);
            list.load(this, false, Sample.CATEGORY_DATA);
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "load category: data");
        }
        else if (id == R.id.nav_priority_data) {
            actionBar.setTitle(R.string.sample_list_title_priority_data);
            list.load(this, false, Sample.CATEGORY_PRIORITY_DATA);
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "load category: priority data");
        }
        else if (id == R.id.nav_important) {
            actionBar.setTitle(R.string.sample_list_title_important);
            list.load(this, false, Sample.CATEGORY_IMPORTANT);
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "load category: important");
        }
        else if (id == R.id.nav_sent) {
            actionBar.setTitle(R.string.sample_list_title_sent);
            list.load(this, false, Sample.CATEGORY_SENT);
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "load category: sent");
        }
        else if (id == R.id.nav_draft) {
            actionBar.setTitle(R.string.sample_list_title_draft);
            list.load(this, false, Sample.CATEGORY_DRAFTS);
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "load category: draft");
        }
        else if (id == R.id.nav_archived) {
            actionBar.setTitle(R.string.sample_list_title_archived);
            list.load(this, false, Sample.CATEGORY_ARCHIVED);
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "load category: archived");
        }
        else if (id == R.id.nav_all) {
            actionBar.setTitle(R.string.sample_list_title_all);
            list.load(this, false, null);
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "load all categories");
        }
        else if (id == R.id.nav_trash) {
            actionBar.setTitle(R.string.sample_list_title_trash);
            list.load(this, true, null);
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "load on trash");
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        switch (requestCode) {
            case REQUEST_ADD:
                if (resultCode == Activity.RESULT_OK) {
                    list.reload(this);
                    listAdapter.notifyDataSetChanged();
                    cancelListSelection();
                    long id = resultIntent.getLongExtra("data.id", 0);
                    if (id != 0) {
                        Intent intent = new Intent(SampleListActivity.this, SampleViewActivity.class);
                        intent.putExtra("data.id", id);
                        startActivityForResult(intent, REQUEST_VIEW);
                    }
                }
                break;

            case REQUEST_VIEW:
                if (resultCode == Activity.RESULT_OK) {
                    boolean changed = resultIntent.getBooleanExtra("data.changed", false);
                    boolean deleted = resultIntent.getBooleanExtra("data.deleted", false);
                    if (changed || deleted) {
                        list.reload(this);
                        listAdapter.notifyDataSetChanged();
                        cancelListSelection();
                    }
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        cancelListSelection();
        savedInstanceState.putString("ui.title", actionBar.getTitle().toString());
        if (list.getDeleted() != null)
            savedInstanceState.putBoolean("data.deleted", list.getDeleted());
        if (list.getCategory() != null)
            savedInstanceState.putInt("data.category", list.getCategory());
        if (list.getQuery() != null)
            savedInstanceState.putString("data.query", list.getQuery());
        savedInstanceState.putInt("data.sortBy", list.getSortBy());
        savedInstanceState.putBoolean("data.sortReverse", list.getSortReverse());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String title = savedInstanceState.getString("ui.title");
        Boolean deleted = null;
        if (savedInstanceState.containsKey("data.deleted"))
            deleted = savedInstanceState.getBoolean("data.deleted");
        Integer category = null;
        if (savedInstanceState.containsKey("data.category"))
            category = savedInstanceState.getInt("data.category");
        String query = null;
        if (savedInstanceState.containsKey("data.query"))
            query = savedInstanceState.getString("data.query");
        int sortBy = savedInstanceState.getInt("data.sortBy");
        boolean sortReverse = savedInstanceState.getBoolean("data.sortReverse");
        actionBar.setTitle(title);
        list.load(this, deleted, category, query, sortBy, sortReverse);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (actionMode) {
            cancelListSelection();
        }
        else {
            super.onBackPressed();
        }
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY).trim();
            Log.d(TAG, "search type: " + query);
            list.search(SampleListActivity.this, query);
            listAdapter.notifyDataSetChanged();
        }
    }

    private void deleteSelected() {
        if (listAdapter.getSelectedItemCount() > 0) {
            List<Long> idList = listAdapter.getSelectedIdList();
            if (idList.size() > 0) {
                SampleDataSource source = new SampleDataSource(this);
                source.open();
                source.deleteList(idList);
                Log.d(TAG, "deleted item(s): " + idList.size());
                for (int i = 0; i < idList.size(); i++)
                    Log.d(TAG, "deleted item: " + idList.get(i));
                source.close();
                list.reload(this);
                listAdapter.notifyDataSetChanged();
            }
            cancelListSelection();
        }
    }

    private void restoreSelected() {
        if (listAdapter.getSelectedItemCount() > 0) {
            List<Long> idList = listAdapter.getSelectedIdList();
            if (idList.size() > 0) {
                SampleDataSource source = new SampleDataSource(this);
                source.open();
                source.restoreList(idList);
                Log.d(TAG, "restore item(s): " + idList.size());
                for (int i = 0; i < idList.size(); i++)
                    Log.d(TAG, "restore item: " + idList.get(i));
                source.close();
                list.reload(this);
                listAdapter.notifyDataSetChanged();
            }
            cancelListSelection();
        }
    }

    private void removeSelected() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.sample_list_dialog_title)
                .setMessage(R.string.sample_list_dialog_message)
                .setNegativeButton(R.string.sample_list_dialog_negative, null)
                .setPositiveButton(R.string.sample_list_dialog_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listAdapter.getSelectedItemCount() > 0) {
                            List<Long> idList = listAdapter.getSelectedIdList();
                            if (idList.size() > 0) {
                                SampleDataSource source = new SampleDataSource(SampleListActivity.this);
                                source.open();
                                source.removeList(idList);
                                Log.d(TAG, "remove item(s): " + idList.size());
                                for (int i = 0; i < idList.size(); i++)
                                    Log.d(TAG, "remove item: " + idList.get(i));
                                source.close();
                                list.reload(SampleListActivity.this);
                                listAdapter.notifyDataSetChanged();
                            }
                            cancelListSelection();
                        }
                    }
                })
                .show();
    }

    private void increaseListSelection() {
        if (!actionMode) {
            actionMode = true;
            saveTitle = actionBar.getTitle().toString();
            toggle.onDrawerOpened(null);
        }
        actionBar.setTitle(String.format(Locale.US, "%d/%d", listAdapter.getSelectedItemCount(), listAdapter.getItemCount()));
        invalidateOptionsMenu();
    }

    private void decreaseListSelection() {
        if (actionMode) {
            if (listAdapter.getSelectedItemCount() > 0)
                actionBar.setTitle(String.format(Locale.US, "%d/%d", listAdapter.getSelectedItemCount(), listAdapter.getItemCount()));
            else
                cancelListSelection();
        }
    }

    private void cancelListSelection() {
        if (actionMode) {
            listAdapter.clearSelections();
            actionBar.setTitle(saveTitle);
            toggle.onDrawerClosed(null);
            actionMode = false;
            invalidateOptionsMenu();
        }
    }

}

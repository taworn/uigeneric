package diy.uigeneric.sampleserver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.Locale;

import diy.restlite.HttpRestLite;
import diy.uigeneric.R;
import diy.uigeneric.adapter.SampleServerListAdapter;
import diy.uigeneric.data.Sample;
import diy.uigeneric.data.SampleServerIndirectList;

/**
 * The SampleServerListActivity is an activity to view and manage Sample(s) data.
 */
public class SampleServerListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = SampleServerListActivity.class.getSimpleName();

    private static final int REQUEST_ADD = 100;
    private static final int REQUEST_VIEW = 101;

    private ActionBar actionBar = null;

    private ProgressDialog progress = null;
    private DialogInterface.OnCancelListener progressCancel = null;
    private HttpRestLite.ResultListener listener = null;
    private boolean loading = false;

    private DrawerLayout drawer = null;
    private SampleServerIndirectList list = null;
    private SampleServerListAdapter listAdapter = null;
    private RecyclerView listView = null;

    private ActionMode actionMode = null;
    private ActionMode.Callback actionModeCallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(R.string.sample_list_title_data);

        // initializes callbacks
        progressCancel = new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                list.cancel();
            }
        };
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

        // initializes FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SampleServerListActivity.this, SampleServerEditActivity.class);
                    startActivityForResult(intent, REQUEST_ADD);
                }
            });
        }

        // initializes navigation drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null)
            navigationView.setNavigationItemSelectedListener(this);

        // initializes RecycledView and its data
        list = new SampleServerIndirectList(this);
        if (savedInstanceState == null) {
            openProgressDialog();
            list.load(false, Sample.CATEGORY_DATA, null, SampleServerIndirectList.SORT_AS_IS, false, listener);
        }
        listAdapter = new SampleServerListAdapter(this, list, new SampleServerListAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Sample item = list.get(position);
                Intent intent = new Intent(SampleServerListActivity.this, SampleServerViewActivity.class);
                intent.putExtra("data.id", item.getId());
                startActivityForResult(intent, REQUEST_VIEW);
            }

            @Override
            public void onLongClick(View view, int position) {
                listAdapter.toggleSelection(position);
                if (listAdapter.getSelected(position))
                    increaseListSelection();
                else
                    decreaseListSelection();
            }
        });
        listView = (RecyclerView) findViewById(R.id.list_view);
        if (listView != null) {
            listView.setHasFixedSize(true);
            listView.setLayoutManager(new LinearLayoutManager(this));
            listView.setItemAnimator(new DefaultItemAnimator());
            listView.setAdapter(listAdapter);
        }

        // initializes contextual action mode
        actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                boolean b = !list.getDeleted();
                mode.getMenuInflater().inflate(R.menu.sample_list_context, menu);
                menu.findItem(R.id.action_restore).setVisible(!b);
                menu.findItem(R.id.action_delete).setVisible(b);
                menu.findItem(R.id.action_remove).setVisible(!b);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
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
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                cancelListSelection();
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        list.cancel();
        if (progress != null)
            progress.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sample_list, menu);

        // initializes radio and check menu items
        MenuItem item;
        switch (list.getSortBy()) {
            default:
            case SampleServerIndirectList.SORT_AS_IS:
                item = menu.findItem(R.id.action_sort_as_is);
                break;
            case SampleServerIndirectList.SORT_NAME:
                item = menu.findItem(R.id.action_sort_name);
                break;
            case SampleServerIndirectList.SORT_NAME_IGNORE_CASE:
                item = menu.findItem(R.id.action_sort_name_ignore_case);
                break;
            case SampleServerIndirectList.SORT_NAME_NATURAL:
                item = menu.findItem(R.id.action_sort_name_natural);
                break;
        }
        item.setChecked(true);
        item = menu.findItem(R.id.action_sort_reverse);
        item.setChecked(list.getSortReverse());

        // paints action_remove_all menu item to color red
        item = menu.findItem(R.id.action_remove_all);
        SpannableString spanString = new SpannableString(item.getTitle());
        spanString.setSpan(new ForegroundColorSpan(Color.RED), 0, spanString.length(), 0);
        item.setTitle(spanString);

        // associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    openProgressDialog();
                    list.search(null, listener);
                    Log.d(TAG, "search stop");
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_sort_as_is) {
            item.setChecked(true);
            list.setSortBy(SampleServerIndirectList.SORT_AS_IS);
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "sort: as is");
            return true;
        }
        else if (id == R.id.action_sort_name) {
            item.setChecked(true);
            list.setSortBy(SampleServerIndirectList.SORT_NAME);
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "sort: name");
            return true;
        }
        else if (id == R.id.action_sort_name_ignore_case) {
            item.setChecked(true);
            list.setSortBy(SampleServerIndirectList.SORT_NAME_IGNORE_CASE);
            listAdapter.notifyDataSetChanged();
            Log.d(TAG, "sort: name ignore case");
            return true;
        }
        else if (id == R.id.action_sort_name_natural) {
            item.setChecked(true);
            list.setSortBy(SampleServerIndirectList.SORT_NAME_NATURAL);
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

        else if (id == R.id.action_remove_all) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.sample_remove_all_dialog_title)
                    .setMessage(R.string.sample_remove_all_dialog_message)
                    .setNegativeButton(R.string.sample_remove_all_dialog_negative, null)
                    .setPositiveButton(R.string.sample_remove_all_dialog_positive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            openProgressDialog();
                            list.removeAll(new HttpRestLite.ResultListener() {
                                @Override
                                public void finish(final HttpRestLite.Result result) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            commonResultTask(result);
                                            if (result.errorCode == 0)
                                                Log.d(TAG, "removed ALL data");
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_data) {
            cancelListSelection();
            actionBar.setTitle(R.string.sample_list_title_data);
            openProgressDialog();
            list.load(false, Sample.CATEGORY_DATA, listener);
            Log.d(TAG, "load category: data");
        }
        else if (id == R.id.nav_priority_data) {
            cancelListSelection();
            actionBar.setTitle(R.string.sample_list_title_priority_data);
            openProgressDialog();
            list.load(false, Sample.CATEGORY_PRIORITY_DATA, listener);
            Log.d(TAG, "load category: priority data");
        }
        else if (id == R.id.nav_important) {
            cancelListSelection();
            actionBar.setTitle(R.string.sample_list_title_important);
            openProgressDialog();
            list.load(false, Sample.CATEGORY_IMPORTANT, listener);
            Log.d(TAG, "load category: important");
        }
        else if (id == R.id.nav_sent) {
            cancelListSelection();
            actionBar.setTitle(R.string.sample_list_title_sent);
            openProgressDialog();
            list.load(false, Sample.CATEGORY_SENT, listener);
            Log.d(TAG, "load category: sent");
        }
        else if (id == R.id.nav_draft) {
            cancelListSelection();
            actionBar.setTitle(R.string.sample_list_title_draft);
            openProgressDialog();
            list.load(false, Sample.CATEGORY_DRAFTS, listener);
            Log.d(TAG, "load category: draft");
        }
        else if (id == R.id.nav_archived) {
            cancelListSelection();
            actionBar.setTitle(R.string.sample_list_title_archived);
            openProgressDialog();
            list.load(false, Sample.CATEGORY_ARCHIVED, listener);
            Log.d(TAG, "load category: archived");
        }
        else if (id == R.id.nav_all) {
            cancelListSelection();
            actionBar.setTitle(R.string.sample_list_title_all);
            openProgressDialog();
            list.load(false, null, listener);
            Log.d(TAG, "load all categories");
        }
        else if (id == R.id.nav_trash) {
            cancelListSelection();
            actionBar.setTitle(R.string.sample_list_title_trash);
            openProgressDialog();
            list.load(true, null, listener);
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
                    long id = resultIntent.getLongExtra("data.id", 0);
                    if (id != 0) {
                        if (list.find(id) < 0) {
                            /*
                            SampleDataSource source = new SampleDataSource(this);
                            source.open();
                            list.add(source.get(id));
                            source.close();
                            listAdapter.notifyDataSetChanged();
                            */
                        }
                        cancelListSelection();
                    }
                }
                break;

            case REQUEST_VIEW:
                if (resultCode == Activity.RESULT_OK) {
                    long id = resultIntent.getLongExtra("data.id", 0);
                    boolean changed = resultIntent.getBooleanExtra("data.changed", false);
                    boolean deleted = resultIntent.getBooleanExtra("data.deleted", false);
                    if (changed || deleted) {
                        if (deleted) {
                            openProgressDialog();
                            list.reload(listener);
                        }
                        else {
                            int i = list.find(id);
                            if (i >= 0) {
                                /*
                                SampleDataSource source = new SampleDataSource(this);
                                source.open();
                                list.edit(i, source.get(id));
                                source.close();
                                */
                            }
                        }
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
        savedInstanceState.putString("ui.title", actionBar.getTitle() != null ? actionBar.getTitle().toString() : "");
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
        openProgressDialog();
        list.load(deleted, category, query, sortBy, sortReverse, listener);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
            openProgressDialog();
            list.search(query, listener);
        }
    }

    private void deleteSelected() {
        if (listAdapter.getSelectedItemCount() > 0) {
            final List<Long> idList = listAdapter.getSelectedIdList();
            if (idList.size() > 0) {
                openProgressDialog();
                list.delete(idList, new HttpRestLite.ResultListener() {
                    @Override
                    public void finish(final HttpRestLite.Result result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                commonResultTask(result);
                                if (result.errorCode == 0) {
                                    Log.d(TAG, "deleted item(s): " + idList.size());
                                    for (int i = 0; i < idList.size(); i++)
                                        Log.d(TAG, "deleted item: " + idList.get(i));
                                    Snackbar snackbar = Snackbar
                                            .make(listView, R.string.sample_deleted_snackbar_message, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.sample_deleted_snackbar_action, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    openProgressDialog();
                                                    list.restore(idList, new HttpRestLite.ResultListener() {
                                                        @Override
                                                        public void finish(final HttpRestLite.Result result) {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    commonResultTask(result);
                                                                    if (result.errorCode == 0) {
                                                                        Log.d(TAG, "undo: restore item(s): " + idList.size());
                                                                        for (int i = 0; i < idList.size(); i++)
                                                                            Log.d(TAG, "undo: restore item: " + idList.get(i));
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                    snackbar.show();
                                }
                            }
                        });
                    }
                });
                cancelListSelection();
            }
        }
    }

    private void restoreSelected() {
        if (listAdapter.getSelectedItemCount() > 0) {
            final List<Long> idList = listAdapter.getSelectedIdList();
            if (idList.size() > 0) {
                openProgressDialog();
                list.restore(idList, new HttpRestLite.ResultListener() {
                    @Override
                    public void finish(final HttpRestLite.Result result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                commonResultTask(result);
                                if (result.errorCode == 0) {
                                    Log.d(TAG, "restore item(s): " + idList.size());
                                    for (int i = 0; i < idList.size(); i++)
                                        Log.d(TAG, "restore item: " + idList.get(i));
                                }
                            }
                        });
                    }
                });
                cancelListSelection();
            }
        }
    }

    private void removeSelected() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.sample_remove_selected_dialog_title)
                .setMessage(R.string.sample_remove_selected_dialog_message)
                .setNegativeButton(R.string.sample_remove_selected_dialog_negative, null)
                .setPositiveButton(R.string.sample_remove_selected_dialog_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listAdapter.getSelectedItemCount() > 0) {
                            final List<Long> idList = listAdapter.getSelectedIdList();
                            if (idList.size() > 0) {
                                openProgressDialog();
                                list.remove(idList, new HttpRestLite.ResultListener() {
                                    @Override
                                    public void finish(final HttpRestLite.Result result) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                commonResultTask(result);
                                                if (result.errorCode == 0) {
                                                    Log.d(TAG, "remove item(s): " + idList.size());
                                                    for (int i = 0; i < idList.size(); i++)
                                                        Log.d(TAG, "remove item: " + idList.get(i));
                                                }
                                            }
                                        });
                                    }
                                });
                                cancelListSelection();
                            }
                        }
                    }
                })
                .show();
    }

    private void increaseListSelection() {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        actionMode.setTitle(String.format(Locale.US, "%d/%d", listAdapter.getSelectedItemCount(), listAdapter.getItemCount()));
    }

    private void decreaseListSelection() {
        if (actionMode != null) {
            actionMode.setTitle(String.format(Locale.US, "%d/%d", listAdapter.getSelectedItemCount(), listAdapter.getItemCount()));
            if (listAdapter.getSelectedItemCount() <= 0)
                cancelListSelection();
        }
    }

    private void cancelListSelection() {
        if (actionMode != null) {
            listAdapter.clearSelections();
            actionMode.finish();
            actionMode = null;
        }
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
        if (result.errorCode == 0) {
            listAdapter.notifyDataSetChanged();
            cancelListSelection();
        }
    }

}

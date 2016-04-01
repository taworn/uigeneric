package diy.uigeneric;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * The main activity.
 */
public class MainActivity extends ActionBarActivity {

    // for log
    private static final String TAG = "MainActivity";

    // for action bar
    private ActionBar actionBar = null;

    // for navigation drawer
    private ActionBarDrawerToggle drawerToggle = null;

    // for Generic fragment
    private GenericListFragment fragment = null;
    private TextView textNoData = null;
    private TextView textStatus = null;

    // for undo
    private UndoFragment fragmentUndo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.show();
        Log.d(TAG, "starting...");

        // Clears all data from trash which too old.
        GenericDataSource source = new GenericDataSource(this);
        source.open();
        source.deleteFromTrash();
        source.close();

        // navigation drawer initialization
        DrawerMenu menu = new DrawerMenu();
        renderMenu(menu);
        ListView drawerList = (ListView) findViewById(R.id.drawer_list);
        DrawerListAdapter drawerAdapter = new DrawerListAdapter(this, menu);
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerList.setAdapter(drawerAdapter);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                drawerLayout.closeDrawers();
                switch (i) {
                    case 1:
                        fragment.load(false, Generic.CATEGORY_DATA, null);
                        actionBar.setTitle(Generic.categoryToString[fragment.getCategory()]);
                        break;
                    case 2:
                        fragment.load(false, Generic.CATEGORY_PRIORITY_DATA, null);
                        actionBar.setTitle(Generic.categoryToString[fragment.getCategory()]);
                        break;
                    case 4:
                        fragment.load(false, Generic.CATEGORY_IMPORTANTS, null);
                        actionBar.setTitle(Generic.categoryToString[fragment.getCategory()]);
                        break;
                    case 5:
                        fragment.load(false, Generic.CATEGORY_SENT, null);
                        actionBar.setTitle(Generic.categoryToString[fragment.getCategory()]);
                        break;
                    case 6:
                        fragment.load(false, Generic.CATEGORY_DRAFTS, null);
                        actionBar.setTitle(Generic.categoryToString[fragment.getCategory()]);
                        break;
                    case 7:
                        fragment.load(false, Generic.CATEGORY_ARCHIVED, null);
                        actionBar.setTitle(Generic.categoryToString[fragment.getCategory()]);
                        break;
                    case 8:
                        fragment.load(false, null, null);
                        actionBar.setTitle("All");
                        break;
                    case 10:
                        fragment.load(true, null, null);
                        actionBar.setTitle("Trash");
                        break;
                }
            }
        });
        drawerAdapter.setOnProfileListener(new DrawerListAdapter.OnProfileListener() {
            @Override
            public void call(Object object) {
                if (object != null)
                    notImplement("Hello, world :)");
            }
        });

        // Generic fragment initialization
        fragment = (GenericListFragment) getFragmentManager().findFragmentById(R.id.fragment_list);
        fragment.setListener(new GenericListFragment.Listener() {
            @Override
            public void onCount(int count) {
                textStatus.setText(String.format("%d item(s)", count));
                if (count > 0)
                    textNoData.setVisibility(View.GONE);
                else
                    textNoData.setVisibility(View.VISIBLE);
            }

            @Override
            public void onItem(long id) {
                Intent intent;
                if (!fragment.getDeleted())
                    intent = new Intent(MainActivity.this, GenericViewActivity.class);
                else
                    intent = new Intent(MainActivity.this, GenericTrashViewActivity.class);
                intent.putExtra("data.generic.id", id);
                intent.putExtra("data.generic.changed", false);
                intent.putExtra("data.generic.deleted", false);
                startActivityForResult(intent, Common.RESULT_EDIT);
                overridePendingTransition(R.animator.anim_slide_left_to_right_enter, R.animator.anim_slide_left_to_right_leave);
            }

            @Override
            public boolean isCreateCAB() {
                fragmentUndo.hide();
                return true;
            }

            @Override
            public boolean isConfirmDeleteSelected() {
                return true;
            }

            @Override
            public void confirmDeleteSelected() {
                if (!fragment.getDeleted())
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Confirm")
                            .setMessage("Are you sure you want to move selected item(s) to trash?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    List<Long> list = fragment.deleteSelected();
                                    if (list != null && list.size() > 0)
                                        fragmentUndo.setDeleted(list);
                                }
                            })
                            .show();
                else
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Confirm")
                            .setMessage("Are you sure you want to delete selected item(s)?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    fragment.deleteSelectedReal();
                                }
                            })
                            .show();
            }
        });
        textNoData = (TextView) findViewById(R.id.text_no_data);
        textStatus = (TextView) findViewById(R.id.text_status);

        // FAB initialization
        ImageButton fab = (ImageButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GenericEditActivity.class);
                startActivityForResult(intent, Common.RESULT_ADD);
                overridePendingTransition(R.animator.anim_slide_left_to_right_enter, R.animator.anim_slide_left_to_right_leave);
            }
        });

        // undo initialization
        fragmentUndo = (UndoFragment) getFragmentManager().findFragmentById(R.id.fragment_undo);
        fragmentUndo.hide();
        fragmentUndo.setListener(new UndoFragment.Listener() {
            @Override
            public void refresh() {
                fragment.load();
            }
        });

        // final initialization
        fragment.load();
        if (savedInstanceState == null)
            actionBar.setTitle(Generic.categoryToString[fragment.getCategory()]);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fragment.load();
        actionBar.setTitle(savedInstanceState.getString("ui.title"));
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("ui.title", actionBar.getTitle() != null ? actionBar.getTitle().toString() : "");
    }

    // This function is for showing icon in menu.  Thank you leRobot. :)
    // More to see http://android-developers.blogspot.in/2012/01/say-goodbye-to-menu-button.html
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                }
                catch (NoSuchMethodException e) {
                    Log.e(TAG, "onMenuOpened", e);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // init radio and check menu items
        MenuItem item;
        switch (fragment.getSortBy()) {
            case GenericListIndirect.SORT_NAME:
                item = menu.findItem(R.id.action_sort_name);
                break;
            case GenericListIndirect.SORT_NAME_IGNORE_CASE:
                item = menu.findItem(R.id.action_sort_name_ignore_case);
                break;
            case GenericListIndirect.SORT_NAME_NATURAL:
                item = menu.findItem(R.id.action_sort_name_natural);
                break;
            case GenericListIndirect.SORT_AS_IS:
            default:
                item = menu.findItem(R.id.action_sort_as_is);
                break;
        }
        item.setChecked(true);
        item = menu.findItem(R.id.action_sort_reverse);
        item.setChecked(fragment.getSortReverse());

        // paints action_delete_all menu item to color red
        item = menu.findItem(R.id.action_delete_all);
        SpannableString spanString = new SpannableString(item.getTitle());
        spanString.setSpan(new ForegroundColorSpan(Color.RED), 0, spanString.length(), 0);
        item.setTitle(spanString);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    fragment.load();
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_test_data:
                new TestData().prepare(this);
                fragment.load();
                Log.d(TAG, "created sample records");
                return true;

            case R.id.action_sort_as_is:
                item.setChecked(true);
                fragment.sort(GenericListIndirect.SORT_AS_IS, fragment.getSortReverse());
                return true;

            case R.id.action_sort_name:
                item.setChecked(true);
                fragment.sort(GenericListIndirect.SORT_NAME, fragment.getSortReverse());
                return true;

            case R.id.action_sort_name_ignore_case:
                item.setChecked(true);
                fragment.sort(GenericListIndirect.SORT_NAME_IGNORE_CASE, fragment.getSortReverse());
                return true;

            case R.id.action_sort_name_natural:
                item.setChecked(true);
                fragment.sort(GenericListIndirect.SORT_NAME_NATURAL, fragment.getSortReverse());
                return true;

            case R.id.action_sort_reverse:
                item.setChecked(!item.isChecked());
                fragment.sort(fragment.getSortBy(), !fragment.getSortReverse());
                return true;

            case R.id.action_delete_all:
                if (fragment.getCount(null, null, null) > 0) {
                    new AlertDialog.Builder(this)
                            .setTitle("Confirm")
                            .setMessage("Are you sure you want to DELETE ALL item(s), include TRASH?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    fragment.deleteAll();
                                }
                            })
                            .show();
                }
                return true;

            case R.id.action_settings:
                notImplement("Settings");
                return true;

            case R.id.action_help:
                notImplement("Help");
                return true;

            case R.id.action_about:
                aboutDialogBox();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        switch (requestCode) {
            case Common.RESULT_ADD:
                if (resultCode == Activity.RESULT_OK) {
                    fragment.load();

                    long id = resultIntent.getLongExtra("data.generic.id", 0);
                    Intent intent = new Intent(MainActivity.this, GenericViewActivity.class);
                    intent.putExtra("data.generic.id", id);
                    startActivityForResult(intent, Common.RESULT_EDIT);
                    overridePendingTransition(R.animator.anim_slide_left_to_right_enter, R.animator.anim_slide_left_to_right_leave);
                }
                break;

            case Common.RESULT_EDIT:
                if (resultCode == Activity.RESULT_OK) {
                    fragment.load();
                    if (resultIntent.getBooleanExtra("data.generic.deleted", false)) {
                        long id = resultIntent.getLongExtra("data.generic.id", 0);
                        ArrayList<Long> list = new ArrayList<>();
                        list.add(id);
                        fragmentUndo.setDeleted(list);
                    }
                }
                break;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            fragment.load(query.trim());
        }
    }

    private void notImplement(String what) {
        Log.d(TAG, "not implement: " + what);
        Toast.makeText(this, what, Toast.LENGTH_SHORT).show();
    }

    private void renderMenu(DrawerMenu menu) {
        menu.list.add(new DrawerMenu.Item("Data", R.drawable.ic_action_read));
        menu.list.add(new DrawerMenu.Item("Priority Data", R.drawable.ic_action_unread));
        menu.list.add(new DrawerMenu.Item("All labels", true));
        menu.list.add(new DrawerMenu.Item("Important", R.drawable.ic_action_important));
        menu.list.add(new DrawerMenu.Item("Sent"));
        menu.list.add(new DrawerMenu.Item("Drafts"));
        menu.list.add(new DrawerMenu.Item("Archived", R.drawable.ic_action_collection));
        menu.list.add(new DrawerMenu.Item("All", R.drawable.ic_launcher));
        menu.list.add(new DrawerMenu.Item("Recycle Bin", true));
        menu.list.add(new DrawerMenu.Item("Trash", R.drawable.ic_action_discard));
    }

    private void aboutDialogBox() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(R.drawable.ic_action_about)
                .setTitle(R.string.about_title)
                .setMessage(R.string.about_message)
                .setNegativeButton("OK", null)
                .show();
    }

}

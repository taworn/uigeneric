package diy.uigeneric.sample;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import diy.uigeneric.R;
import diy.uigeneric.adapter.SampleListAdapter;
import diy.uigeneric.data.Sample;
import diy.uigeneric.data.SampleIndirectList;

public class SampleListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = SampleListActivity.class.getSimpleName();

    private static final int REQUEST_ADD = 100;
    private static final int REQUEST_VIEW = 101;

    private SampleIndirectList list = null;
    private RecyclerView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        list = new SampleIndirectList();
        list.load(this, null, null, null, SampleIndirectList.SORT_AS_IS, false);

        listView = (RecyclerView) findViewById(R.id.list_view);
        if (listView != null) {
            listView.setHasFixedSize(true);
            listView.setLayoutManager(new LinearLayoutManager(this));
            listView.setItemAnimator(new DefaultItemAnimator());
            listView.setAdapter(new SampleListAdapter(this, list, new SampleListAdapter.OnItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Sample item = list.get(position);
                    Intent intent = new Intent(SampleListActivity.this, SampleViewActivity.class);
                    intent.putExtra("data.id", item.getId());
                    startActivityForResult(intent, REQUEST_VIEW);
                }
            }));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
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
                    listView.getAdapter().notifyDataSetChanged();
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
            list.setSortBy(SampleIndirectList.SORT_AS_IS);
            listView.getAdapter().notifyDataSetChanged();
            return true;
        }
        else if (id == R.id.action_sort_name) {
            item.setChecked(true);
            list.setSortBy(SampleIndirectList.SORT_NAME);
            listView.getAdapter().notifyDataSetChanged();
            return true;
        }
        else if (id == R.id.action_sort_name_ignore_case) {
            item.setChecked(true);
            list.setSortBy(SampleIndirectList.SORT_NAME_IGNORE_CASE);
            listView.getAdapter().notifyDataSetChanged();
            return true;
        }
        else if (id == R.id.action_sort_name_natural) {
            item.setChecked(true);
            list.setSortBy(SampleIndirectList.SORT_NAME_NATURAL);
            listView.getAdapter().notifyDataSetChanged();
            return true;
        }
        else if (id == R.id.action_sort_reverse) {
            item.setChecked(!item.isChecked());
            list.setSortReverse(!list.getSortReverse());
            listView.getAdapter().notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        }
        else if (id == R.id.nav_gallery) {

        }
        else if (id == R.id.nav_slideshow) {

        }
        else if (id == R.id.nav_manage) {

        }
        else if (id == R.id.nav_share) {

        }
        else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                    listView.getAdapter().notifyDataSetChanged();
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
                        listView.getAdapter().notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            list.search(SampleListActivity.this, query.trim());
            listView.getAdapter().notifyDataSetChanged();
        }
    }

}

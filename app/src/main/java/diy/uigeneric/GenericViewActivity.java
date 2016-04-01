package diy.uigeneric;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * This class provides view object activity.
 */
public class GenericViewActivity extends ActionBarActivity {

    private boolean changed = false;

    private GenericViewFragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.show();

        changed = savedInstanceState != null && savedInstanceState.getBoolean("data.generic.changed");

        fragment = (GenericViewFragment) getFragmentManager().findFragmentById(R.id.fragment_view);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            long id = bundle.getLong("data.generic.id", 0);
            fragment.setItemId(id);
        }

        actionBar.setTitle("View Item");
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("data.generic.changed", changed);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_generic_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                back();
                return true;

            case R.id.action_delete:
                delete();
                return true;

            case R.id.action_edit:
                edit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        switch (requestCode) {
            case Common.RESULT_EDIT:
                if (resultCode == Activity.RESULT_OK) {
                    fragment.reload();
                    changed = true;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.animator.anim_slide_right_to_left_enter, R.animator.anim_slide_right_to_left_leave);
    }

    private void back() {
        if (changed) {
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
        }
        finish();
    }

    private void delete() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to move this item to trash?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        resultIntent.putExtra("data.generic.id", fragment.getItemId());
                        resultIntent.putExtra("data.generic.deleted", true);
                        fragment.delete();
                        finish();
                    }
                })
                .show();
    }

    private void edit() {
        Intent intent = new Intent(this, GenericEditActivity.class);
        intent.putExtra("data.generic.id", fragment.getItemId());
        startActivityForResult(intent, Common.RESULT_EDIT);
        overridePendingTransition(R.animator.anim_slide_left_to_right_enter, R.animator.anim_slide_left_to_right_leave);
    }

}

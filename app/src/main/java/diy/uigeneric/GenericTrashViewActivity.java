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
 * This class provides view in trash activity.
 */
public class GenericTrashViewActivity extends ActionBarActivity {

    private GenericViewFragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_trash_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.show();

        fragment = (GenericViewFragment) getFragmentManager().findFragmentById(R.id.fragment_view);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            long id = bundle.getLong("data.generic.id", 0);
            fragment.setItemId(id);
        }

        actionBar.setTitle("View Item (Trash)");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_generic_trash_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                back();
                return true;

            case R.id.action_restore:
                restore();
                return true;

            case R.id.action_delete:
                delete();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        finish();
    }

    private void restore() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        fragment.restore();
        finish();
    }

    private void delete() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to delete this item?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        fragment.deleteReal();
                        finish();
                    }
                })
                .show();
    }

}

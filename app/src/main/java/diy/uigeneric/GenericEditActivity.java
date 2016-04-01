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
 * This class provides edit object activity.
 */
public class GenericEditActivity extends ActionBarActivity {

    private GenericEditFragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_edit);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.show();

        fragment = (GenericEditFragment) getFragmentManager().findFragmentById(R.id.fragment_edit);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            long id = bundle.getLong("data.generic.id", 0);
            fragment.setItemId(id);
        }
        if (fragment.getItemId() > 0)
            actionBar.setTitle("Edit Item");
        else
            actionBar.setTitle("Add New Item");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_generic_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                back();
                return true;

            case R.id.action_save:
                save();
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
        if (fragment.changed())
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to go back without save?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .show();
        else
            finish();
    }

    private void save() {
        if (!fragment.empty()) {
            if (fragment.changed()) {
                fragment.save();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("data.generic.id", fragment.getItemId());
                setResult(Activity.RESULT_OK, resultIntent);
            }
            finish();
        }
    }

}

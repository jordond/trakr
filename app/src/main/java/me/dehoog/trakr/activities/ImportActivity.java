package me.dehoog.trakr.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.dehoog.trakr.R;
import me.dehoog.trakr.adapters.CheckInListAdapter;
import me.dehoog.trakr.models.Purchase;
import me.dehoog.trakr.models.User;

public class ImportActivity extends ActionBarActivity {

    @InjectView(R.id.check_in_list)
    ListView mListView;
    private CheckInListAdapter mAdapter;

    @InjectView(R.id.action_done) FloatingActionButton mFab;

    private User mUser;
    private List<Purchase> mCheckIns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        ButterKnife.inject(this);

        mFab.attachToListView(mListView);

        contactServer();
    }

    private void contactServer() {

        // Simulate network lag

        // Get

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_import, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

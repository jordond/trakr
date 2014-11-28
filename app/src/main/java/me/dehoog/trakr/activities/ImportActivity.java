package me.dehoog.trakr.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.dehoog.trakr.R;
import me.dehoog.trakr.adapters.CheckInListAdapter;
import me.dehoog.trakr.adapters.ImportAdapter;
import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.ImportResult;
import me.dehoog.trakr.models.Purchase;
import me.dehoog.trakr.models.User;

public class ImportActivity extends ActionBarActivity {

    private static final String TAG = ImportActivity.class.getName();

    @InjectView(R.id.check_in_list)
    ListView mListView;
    private ImportAdapter mAdapter;

    @InjectView(R.id.action_done) FloatingActionButton mFab;

    private User mUser;
    private List<Purchase> mCheckIns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        String email = settings.getString("email", "none");
        mUser = new User().findUser(email);

        ButterKnife.inject(this);
        mFab.attachToListView(mListView);

        contactServer();
    }

    private void contactServer() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Contacting bank...");
        try {

            Thread.sleep(2000); // Simulate network lag

            InputStream inputStream = getAssets().open("bank_response.json");
            byte[] buffer = new byte[inputStream.available()];

            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");

            Gson gson = new Gson();
            ImportResult importResult = gson.fromJson(json, ImportResult.class);

            processResult(importResult);

        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
        dialog.dismiss();
    }

    public void processResult(ImportResult result) {
        ImportResult.Message response = result.getSCSMSG();

        Account exists = new Account().findAccount(response.getAccount_num());
        if (exists == null) {
            addAccount();
        } else {
            processCheckIns();
        }
    }

    private void addAccount() {
    }

    private void processCheckIns() {
        // process the list of results

        // setup the list adapter
    }

}

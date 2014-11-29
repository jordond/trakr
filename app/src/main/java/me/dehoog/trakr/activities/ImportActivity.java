package me.dehoog.trakr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.dehoog.trakr.R;
import me.dehoog.trakr.adapters.CheckInListAdapter;
import me.dehoog.trakr.adapters.ImportAdapter;
import me.dehoog.trakr.fragments.AccountManagerFragment;
import me.dehoog.trakr.interfaces.AddAccountInteraction;
import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.ImportResult;
import me.dehoog.trakr.models.ImportResult.Transaction;
import me.dehoog.trakr.models.Purchase;
import me.dehoog.trakr.models.User;
import me.dehoog.trakr.tasks.ImportTask;

public class ImportActivity extends FragmentActivity {

    private static final String TAG = ImportActivity.class.getName();

    @InjectView(R.id.check_in_list)
    ListView mListView;
    private ImportAdapter mAdapter;

    @InjectView(R.id.action_done) FloatingActionButton mFab;
    @OnClick(R.id.action_done)
    public void done() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private User mUser;
    private List<Purchase> mCheckIns;

    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        String email = settings.getString("email", "none");
        mUser = new User().findUser(email);

        ButterKnife.inject(this);
        mFab.attachToListView(mListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new MaterialDialog.Builder(this)
                .title("Sync Accounts")
                .content("Would you like to sync your accounts?")
                .positiveText("Yes")
                .negativeText("Cancel")
                .callback(new MaterialDialog.Callback() {
                  @Override
                  public void onNegative(MaterialDialog materialDialog) {
                      setResult(RESULT_CANCELED);
                      finish();
                  }

                  @Override
                  public void onPositive(MaterialDialog materialDialog) {
                      contactServer();
                  }
                })
                .show();
    }

    private void contactServer() {
        ImportTask importTask = new ImportTask(this, new ImportTask.OnImportResult() {
            @Override
            public void onResult(ImportResult importResult) {
                if (importResult.getStatus() == 200) {
                    processResult(importResult);
                } else {
                    new MaterialDialog.Builder(ImportActivity.this)
                            .title("Error")
                            .content("Something went wrong: " + importResult.getStatus_message())
                            .positiveText("OK")
                            .callback(new MaterialDialog.SimpleCallback() {
                                @Override
                                public void onPositive(MaterialDialog materialDialog) {
                                    setResult(RESULT_CANCELED);
                                    finish();
                                }
                            }).show();
                }
            }
        });
        importTask.execute();
    }

    public void processResult(ImportResult result) {
        final ImportResult.Message response = result.getSCSMSG();

        Account exists = new Account().findAccount(response.getAccount_num());
        if (exists == null) {
            new MaterialDialog.Builder(this)
                    .title("New Account Found!")
                    .content("A new account was found, press OK to add this account, then import the new transactions.")
                    .positiveText("OK")
                    .callback(new MaterialDialog.SimpleCallback() {
                        @Override
                        public void onPositive(MaterialDialog materialDialog) {
                            addAccount(response.getAccount_num(), response.getTag().getTransactions());
                            materialDialog.dismiss();
                        }
                    }).show();
        } else {
            new MaterialDialog.Builder(this)
                    .title("New Transactions Found!")
                    .content("Adding new transactions to '" + exists.getDescription() + "'")
                    .positiveText("OK")
                    .show();
            processCheckIns(exists, response.getTag().getTransactions());
        }
    }

    private void addAccount(String accountNumber, final List<Transaction> transactions) {
        final Account account = new Account(mUser);
        account.setNumber(accountNumber);

        AccountManagerFragment fragment = AccountManagerFragment.newInstance(mUser, "add", account);
        fragment.setmListener(new AddAccountInteraction() {
            @Override
            public void onAddInteraction() {
                processCheckIns(account, transactions);
            }
        });

        ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out);
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void processCheckIns(Account account, List<Transaction> transactions) {
        List<Purchase> checkIns = new ArrayList<Purchase>();
        for (Transaction t : transactions) {
            if (!Purchase.exists(t.getKey())) {
                double amount = Double.valueOf(t.getAmount());
                Purchase p = new Purchase(account, amount);
                checkIns.add(p);
            } else {
                Log.d(TAG, "ProcessCheckIns: Skipping existing purchase " + t.getDescription() + " with key: " + t.getKey());
            }
        }

        Log.d(TAG, "ProcessCheckIns: Found " + checkIns.size() + " new transactions");

        mAdapter = new ImportAdapter(this, checkIns);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // call fragment
            }
        });
    }

}

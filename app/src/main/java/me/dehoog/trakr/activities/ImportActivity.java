package me.dehoog.trakr.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.MapFragment;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.dehoog.trakr.R;
import me.dehoog.trakr.adapters.ImportAdapter;
import me.dehoog.trakr.fragments.AccountManagerFragment;
import me.dehoog.trakr.fragments.ImportMapFragment;
import me.dehoog.trakr.interfaces.AddAccountInteraction;
import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.ImportResult;
import me.dehoog.trakr.models.ImportResult.Transaction;
import me.dehoog.trakr.models.Merchant;
import me.dehoog.trakr.models.Purchase;
import me.dehoog.trakr.models.User;
import me.dehoog.trakr.tasks.ImportTask;

/*
    ImportActivity
    A late addition to trakR, this allows the user to "import/sync" their information
    with external sources.  Right now the Async task this uses simulates network lag,
    and just reads in a local JSON file.  This activity can handle new or existing accounts
    or transactions. Skipping the accounts, or transactions that already exist.
 */
public class ImportActivity extends FragmentActivity {

    private static final String TAG = ImportActivity.class.getName();

    @InjectView(R.id.check_in_list)
    ListView mListView;
    private ImportAdapter mAdapter;
    private List<Purchase> mCheckins;

    @InjectView(R.id.action_done) FloatingActionButton mFab;
    @OnClick(R.id.action_done)
    public void done() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private User mUser;
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
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Sync Accounts")
                .content("Would you like to sync your accounts?")
                .positiveText("Yes")
                .negativeText("Cancel")
                .callback(new MaterialDialog.Callback() {
                  @Override
                  public void onNegative(MaterialDialog materialDialog) {
                      materialDialog.dismiss();
                      setResult(RESULT_CANCELED);
                      finish();
                  }

                  @Override
                  public void onPositive(MaterialDialog materialDialog) {
                      materialDialog.dismiss();
                      contactServer();
                  }
                }).build();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

        private void contactServer() { // air quotes "server", don't lie to me Mr. Method, I know your truths
        ImportTask importTask = new ImportTask(this, new ImportTask.OnImportResult() {
            @Override
            public void onResult(ImportResult importResult) {
                if (importResult.getStatus() == 200) {
                    processResult(importResult);
                } else {
                   displayErrorDialog(importResult.getStatus_message());
                }
            }
        });
        importTask.execute();
    }

    public void processResult(ImportResult result) {
        final ImportResult.Message response = result.getSCSMSG(); // Thats the JSON -> Java Model object, GSON ftw

        final Account exists = new Account().findAccount(response.getAccount_num()); // detect duplicate accounts
        if (exists == null) {
            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title("New Account Found!")
                    .content("A new account was found, press OK to add this account, then import the new transactions.")
                    .positiveText("OK")
                    .callback(new MaterialDialog.SimpleCallback() {
                        @Override
                        public void onPositive(MaterialDialog materialDialog) {
                            addAccount(response.getAccount_num(), response.getTag().getTransactions());
                            materialDialog.dismiss();
                        }
                    }).build();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
           final MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title("New Transactions Found!")
                    .content("Adding new transactions to '" + exists.getDescription() + "'")
                    .positiveText("OK")
                    .callback(new MaterialDialog.SimpleCallback() {
                        @Override
                        public void onPositive(MaterialDialog materialDialog) {
                            materialDialog.dismiss();
                            processCheckIns(exists, response.getTag().getTransactions());
                        }
                    }).build();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private void addAccount(String accountNumber, final List<Transaction> transactions) {
        final Account account = new Account(mUser);
        account.setNumber(accountNumber);

        AccountManagerFragment fragment = AccountManagerFragment.newInstance(mUser, "add", account);
        fragment.hideCloseButton(true);
        fragment.setmListener(new AddAccountInteraction() {
            @Override
            public void onAddInteraction() {
                processCheckIns(account, transactions);
            }
        });

        ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out);
        ft.replace(R.id.content, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void processCheckIns(Account account, List<Transaction> transactions) {
        List<Purchase> checkIns = new ArrayList<Purchase>();
        for (Transaction t : transactions) {
            if (!Purchase.exists(t.getKey())) {     // detect duplicate transactions
                String sAmount = t.getAmount();
                sAmount = sAmount.substring(0, sAmount.length() - 2) + "." + sAmount.substring(sAmount.length() - 2, sAmount.length());
                double amount = Double.valueOf(sAmount);
                Purchase p = new Purchase(account, amount);
                Merchant m = new Merchant(t.getDescription());
                p.setKey(t.getKey());   // the unique identifier for each transaction
                p.setAccount(account);
                p.setMerchant(m);
                checkIns.add(p);
            } else {
                Log.d(TAG, "ProcessCheckIns: Skipping existing purchase " + t.getDescription() + " with key: " + t.getKey());
            }
        }
        Log.d(TAG, "ProcessCheckIns: Found " + checkIns.size() + " new transactions");
        setupAdapter(checkIns);
    }

    private void setupAdapter(List<Purchase> checkIns) {
        mCheckins = checkIns;
        mAdapter = new ImportAdapter(this, mCheckins);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchMap((Purchase) mAdapter.getItem(position));
            }
        });
    }

    private void launchMap(final Purchase checkIn) {
        ImportMapFragment fragment = ImportMapFragment.newInstance(mUser.getId(), checkIn);
        fragment.setFragmentManager(getFragmentManager());
        fragment.setOnCheckIn(new ImportMapFragment.OnCheckedIn() {
            @Override
            public void onConfirmCheckIn() {
                MaterialDialog dialog = new MaterialDialog.Builder(ImportActivity.this)
                        .title("Success!")
                        .content("Successfully checked in at '" + checkIn.getMerchant().getName() + "'")
                        .positiveText("OK")
                        .callback(new MaterialDialog.SimpleCallback() {
                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                mCheckins.remove(checkIn);
                                mAdapter = new ImportAdapter(ImportActivity.this, mCheckins);
                                mListView.setAdapter(mAdapter);
                                materialDialog.dismiss();

                            }
                        }).build();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                removeMapId();
            }
            @Override
            public void onCancelCheckIn() {

            }
            @Override
            public void onNoResults(String message) {
                displayNoResultsDialog(checkIn, message);
            }
            @Override
            public void onError(String message) {
                displayErrorDialog(message);
            }
        });
        ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out);
        ft.replace(R.id.content, fragment, "ImportMap");
        ft.addToBackStack(null);
        ft.commit();

    }

    public void displayErrorDialog(String message) {
        MaterialDialog dialog = new MaterialDialog.Builder(ImportActivity.this)
                .title("Error")
                .content("Something went wrong: " + message)
                .positiveText("OK")
                .callback(new MaterialDialog.SimpleCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        materialDialog.dismiss();
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }).build();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void displayNoResultsDialog(final Purchase checkIn, String message) {
        MaterialDialog dialog = new MaterialDialog.Builder(ImportActivity.this)
                .title("No Results")
                .content(message)
                .positiveText("Keep")
                .negativeText("Delete")
                .callback(new MaterialDialog.Callback() {
                    @Override
                    public void onNegative(MaterialDialog materialDialog) {
                        mCheckins.remove(checkIn);
                        mAdapter = new ImportAdapter(ImportActivity.this, mCheckins);
                        mListView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        materialDialog.dismiss();
                    }
                }).build();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void removeMapId() {
        MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        if (fragment != null) {
            Log.d(TAG, "Removing existing Map from the fragment stack");
            getFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        final ImportMapFragment fragment = (ImportMapFragment) getSupportFragmentManager().findFragmentByTag("ImportMap");

        if (fragment != null) {
            fragment.onBackPressed();
            removeMapId();
        } else {
            Log.d(TAG, "onBack Pressed: Map fragment null, passing to super");
            super.onBackPressed();
        }
    }
}

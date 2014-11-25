package me.dehoog.trakr.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import me.dehoog.trakr.R;
import me.dehoog.trakr.adapters.MainPagerAdapter;
import me.dehoog.trakr.fragments.AccountManagerFragment;
import me.dehoog.trakr.interfaces.AccountsInteraction;
import me.dehoog.trakr.interfaces.AddAccountInteraction;
import me.dehoog.trakr.interfaces.CheckInsInteraction;
import me.dehoog.trakr.interfaces.EditAccountCallback;
import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.Purchase;
import me.dehoog.trakr.models.User;


public class MainActivity extends FragmentActivity implements AccountsInteraction,      // Interface callback for Accounts Card view fragment
                                                              AddAccountInteraction,    // Callback for adding, and editing account
                                                              EditAccountCallback,      // Button click inside AccountCard
        CheckInsInteraction {

    public static final String PREFS_NAME = "TrakrPrefs";
    public static final int CHECK_IN_REQUEST = 1;
    public SharedPreferences mSettings;

    public User mUser;

    private MainPagerAdapter mAdapter;

    private FragmentTransaction ft;
    private AccountManagerFragment mAccountManager;

    // Tab pager components
    @InjectView(R.id.tabs) public PagerSlidingTabStrip mTabs;
    @InjectView(R.id.pager) public ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSettings = getSharedPreferences(PREFS_NAME, 0);

        String email = mSettings.getString("email", "none"); //TODO replaced intent extras with shared prefs, keep?
        mUser = new User().findUser(email);
        if (mUser == null) {
            logout();
        }

        ButterKnife.inject(this); // get all dem views
        mAdapter = new MainPagerAdapter(getSupportFragmentManager(), mUser);
        mPager.setAdapter(mAdapter);
        mTabs.setViewPager(mPager);

    }// onCreate

    private void logout() {
        mSettings.edit()
                .remove("loggedIn")
                .remove("email")
                .apply();

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ButterKnife.inject(this);
    }// onStart

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_profile:
                break;
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActionBar() != null) {
            getActionBar().setTitle("");
        }
    }

    @Override
    public void onAccountsInteraction(String action, Account account) { // Accounts Fragment
        mAccountManager = AccountManagerFragment.newInstance(mUser, action, account);
        ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out);
        ft.replace(R.id.container, mAccountManager,"AddAccountTag");
        ft.addToBackStack(null);
        ft.commit();
    }

    // Fragment callbacks

    @Override
    public void onAddInteraction() {    // Accounts Manager Fragment
        mPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void editButton(Account account) {   // Accounts Fragment, AccountCard edit button
        onAccountsInteraction("edit", account);
    }

    @Override
    public void onCheckInsInteraction() {  // Recent transaction, action button
        Intent intent = new Intent(this, CheckInActivity.class);
        startActivityForResult(intent, CHECK_IN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHECK_IN_REQUEST) {
            if (resultCode == RESULT_OK) {

                //mAdapter.getCheckInsFragment().setupList();
                mPager.getAdapter().notifyDataSetChanged();
            }
            mPager.setCurrentItem(1);
        }
    }
}

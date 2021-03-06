package me.dehoog.trakr.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import me.dehoog.trakr.R;
import me.dehoog.trakr.adapters.MainPagerAdapter;
import me.dehoog.trakr.cards.ExpandAccountCard;
import me.dehoog.trakr.fragments.AccountManagerFragment;
import me.dehoog.trakr.fragments.CheckInViewerFragment;
import me.dehoog.trakr.interfaces.AccountsInteraction;
import me.dehoog.trakr.interfaces.AddAccountInteraction;
import me.dehoog.trakr.interfaces.CheckInsInteraction;
import me.dehoog.trakr.interfaces.EditAccountCallback;
import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.Purchase;
import me.dehoog.trakr.models.User;

/*
    MainActivity
    Ah yes, main activity.  It should really be called Shell Activity, as it really doesn't do much,
    it just sets up the view pager and listens for events from those fragments.  One of the first
    activities created, so it is mighty sloppy and confusing.
 */
public class MainActivity extends FragmentActivity implements AccountsInteraction,                  // Interface callback for Accounts Card view fragment
                                                              EditAccountCallback,                  // Button click inside AccountCard
                                                              CheckInsInteraction,                  // Action performed in check in tab
                                                              ExpandAccountCard.ExpandListClick {   // Check clicked in expanded card

    public static final String PREFS_NAME = "TrakrPrefs";
    public static final int CHECK_IN_REQUEST = 1;
    public static final int IMPORT_REQUEST = 2;
    public SharedPreferences mSettings;

    public User mUser;

    private FragmentTransaction ft;

    // Tab pager components
    @InjectView(R.id.tabs) public PagerSlidingTabStrip mTabs;
    @InjectView(R.id.pager) public ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSettings = getSharedPreferences(PREFS_NAME, 0);

        String email = mSettings.getString("email", "none");
        mUser = new User().findUser(email);
        if (mUser == null) {
            logout();
        }

        ButterKnife.inject(this); // get all dem views
        MainPagerAdapter mAdapter = new MainPagerAdapter(getSupportFragmentManager(), mUser);
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(2);
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
            case R.id.action_sync:
                Intent intent = new Intent(this, ImportActivity.class);
                startActivityForResult(intent, IMPORT_REQUEST);
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
        AccountManagerFragment mAccountManager = AccountManagerFragment.newInstance(mUser, action, account);
        mAccountManager.setmListener(new AddAccountInteraction() {
            @Override
            public void onAddInteraction() {
                mPager.getAdapter().notifyDataSetChanged();
            }
        });
        ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out);
        ft.replace(R.id.container, mAccountManager);
        ft.addToBackStack(null);
        ft.commit();
    }

    // Fragment callbacks
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
    public void expandListItemClicked(Purchase purchase) {
        CheckInViewerFragment fragment = CheckInViewerFragment.newInstance(purchase);
        launchFragment(fragment);
    }

    @Override
    public void onShowViewer(Purchase purchase) {
        CheckInViewerFragment fragment = CheckInViewerFragment.newInstance(purchase);
        launchFragment(fragment);
    }

    public void launchFragment(Fragment fragment) {
        ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_down, R.animator.slide_in_up, R.animator.slide_out_down);
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHECK_IN_REQUEST) {
            if (resultCode == RESULT_OK) {
                mPager.getAdapter().notifyDataSetChanged();
            }
            mPager.setCurrentItem(1);
        } else if (requestCode == IMPORT_REQUEST) {
            mPager.getAdapter().notifyDataSetChanged();
        }
    }
}

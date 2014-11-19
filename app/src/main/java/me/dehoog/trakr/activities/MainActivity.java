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

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import me.dehoog.trakr.R;
import me.dehoog.trakr.adapters.MainPagerAdapter;
import me.dehoog.trakr.fragments.AddAccountFragment;
import me.dehoog.trakr.fragments.MainTabsFragment;
import me.dehoog.trakr.interfaces.AccountsInteraction;
import me.dehoog.trakr.models.User;


public class MainActivity extends FragmentActivity implements AccountsInteraction {

    public static final String PREFS_NAME = "TrakrPrefs";
    public SharedPreferences mSettings;

    public User mUser;

    private FragmentTransaction ft;
    private MainTabsFragment mMainTabs;
    private AddAccountFragment mAddAccount;

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

        mMainTabs = MainTabsFragment.newInstance(mUser);

        ButterKnife.inject(this); // get all dem views
        MainPagerAdapter mAdapter = new MainPagerAdapter(getSupportFragmentManager(), mUser);
        mPager.setAdapter(mAdapter);
        mTabs.setViewPager(mPager);

        //TODO get rid of tabs in fragment?
//        ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.container, mMainTabs);
//        ft.commit();

        //TODO display crouton asking to setup profile, if User.isFirstLogin()
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
    public void onAccountsInteraction() {
        mAddAccount = AddAccountFragment.newInstance(mUser);
        ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out);
        ft.replace(R.id.container, mAddAccount,"AddAccountTag");
        ft.addToBackStack(null);
        ft.commit();
    }
}

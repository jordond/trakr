package me.dehoog.trakr.activities;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
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

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, mMainTabs);
        ft.commit();

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
        ft.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);
        ft.add(R.id.container, mAddAccount,"AddAccountTag");
        ft.addToBackStack(null);
        ft.commit();
    }
}

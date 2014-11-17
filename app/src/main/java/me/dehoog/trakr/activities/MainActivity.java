package me.dehoog.trakr.activities;

import android.app.Activity;
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
import me.dehoog.trakr.models.User;


public class MainActivity extends FragmentActivity {

    public static final String PREFS_NAME = "TrakrPrefs";
    public SharedPreferences mSettings;

    public User mUser;

    // Tab pager components
    @InjectView(R.id.tabs) public PagerSlidingTabStrip mTabs;
    @InjectView(R.id.pager) public ViewPager mPager;
    private MainPagerAdapter mAdapter;

    // UI Components

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
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
}

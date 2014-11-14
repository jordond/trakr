package me.dehoog.trakr.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.User;


public class MainActivity extends Activity {

    public static final String PREFS_NAME = "TrakrPrefs";
    public SharedPreferences mSettings;

    public User mUser;

    // UI Components
    @InjectView(R.id.test) TextView mTestTextView;

    @OnClick(R.id.buttontest) void onClick() {
        logout();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSettings = getSharedPreferences(PREFS_NAME, 0);

        mUser = (User) getIntent().getSerializableExtra("user");
        if (mUser == null) {
            logout();
        } else {
            if (getIntent().getBooleanExtra("loggingIn", false)) {
                Crouton.makeText(this, "Signed into TrakR as " + mUser.getUsername() + "!", Style.CONFIRM).show();
            } else {
                Crouton.makeText(this, "Welcome back " + mUser.getUsername() + "!", Style.INFO).show();
            }
        }

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
        mTestTextView.setText("EMAIL: " + mUser.getEmail() + " PASSWORD: " + mUser.getPassword());

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

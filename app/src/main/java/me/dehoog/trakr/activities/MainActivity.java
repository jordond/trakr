package me.dehoog.trakr.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.User;


public class MainActivity extends Activity {

    public static final String PREFS_NAME = "TrakrPrefs";
    public static final int REQUEST_LOGIN_CODE = 0;

    public boolean mLoggedIn = false;
    public boolean mLoggingOut = false;
    public User mUser;

    // UI Components
    @InjectView(R.id.test) TextView mTestTextView;
    @InjectView(R.id.buttontest) Button mTestButton;

    @OnClick(R.id.buttontest) void onClick() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("loggedIn").apply();
        editor.remove("email").apply();
        mUser = null;
        mLoggedIn = false;
        mLoggingOut = true;
        login();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }// onCreate


    @Override
    protected void onStart() {
        super.onStart();

        getLoggedInState();
        if (!mLoggedIn) {
            login();
        } else {
            ButterKnife.inject(this);
            mTestTextView.setText("EMAIL: " + mUser.getEmail() + " PASSWORD: " + mUser.getPassword());
        }

    }// onStart
    //TODO need to set mUser to the object from shared prefs, as it null apparently.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOGIN_CODE) {
            if (resultCode == RESULT_OK) {
                mUser = (User) data.getSerializableExtra("user");
            }
        }
    }


    public void getLoggedInState() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mLoggedIn = settings.getBoolean("loggedIn", false);
        if (mLoggedIn) {
            if (mUser == null) {
                mUser = new User().findUser(settings.getString("email", "none"));
                if (mUser == null) {
                    mLoggedIn = false;
                } else {
                    Crouton.makeText(this, getString(R.string.message_logged_in) + " " + mUser.getEmail(), Style.INFO).show();
                }
            } else {
                Crouton.makeText(this, mUser.getEmail() + " " + getString(R.string.message_login_success), Style.CONFIRM).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("loggedIn", mLoggedIn);
        if (mLoggedIn) {
            editor.putString("email", mUser.getEmail());
        }
        editor.apply();
    }

    public void login() {
        Intent i = new Intent(getApplication(), LoginActivity.class);
        i.putExtra("loggingOut", mLoggingOut);
        startActivityForResult(i, REQUEST_LOGIN_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Crouton.cancelAllCroutons();
    }
}

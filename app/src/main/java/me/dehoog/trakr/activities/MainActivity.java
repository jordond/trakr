package me.dehoog.trakr.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import me.dehoog.trakr.R;
import me.dehoog.trakr.models.User;


public class MainActivity extends Activity {

    public static final String PREFS_NAME = "TrakrPrefs";
    public boolean mLoggedIn = false;
    public User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mLoggedIn = settings.getBoolean("loggedIn", false);
        if (mLoggedIn) {
            String email = settings.getString("userEmail", "none");
            if (email != "none") {
                mUser.find(User.class, "email = ?", email);
                if (mUser.getId() == 0) {
                    mLoggedIn = false;
                }
            }
        }
    }// onCreate

    @Override
    protected void onStart() {
        super.onStart();

        if (!mLoggedIn) {
            Intent i = new Intent(getApplication(), LoginActivity.class);
            startActivity(i);
        }
    }// onStart

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("loggedIn", mLoggedIn);
        if (mLoggedIn) {
            editor.putString("email", mUser.getEmail());
        }
        editor.commit();
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
}

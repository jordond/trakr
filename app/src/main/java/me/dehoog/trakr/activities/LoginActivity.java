package me.dehoog.trakr.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import me.dehoog.trakr.R;
import me.dehoog.trakr.fragments.LoginFragment;
import me.dehoog.trakr.models.User;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoginFragment.OnFragmentInteractionListener {

    private UserLoginTask mAuthTask = null;
    private User mUser = new User();

    private FragmentTransaction ft;
    private LoginFragment mLoginFragment;

//    // DEBUG CODE
//    @OnClick(R.id.debug_login_button) void debugLogin() {
//        //debug user
//        mUser = mUser.findUser("t@t");
//        if (mUser == null) {
//            mUser = new User("t@t", "test123");
//            mUser.save();
//        }
//        mAuthTask = new UserLoginTask(this, "t@t", "test123");
//        mAuthTask.execute((Void) null);
//    }
//    // DEBUG CODE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginFragment = mLoginFragment.newInstance();

        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_login, mLoginFragment);
        ft.commit();

        boolean loggingOut = getIntent().getExtras().getBoolean("loggingOut");
        if (loggingOut) {
           Crouton.makeText(this, R.string.message_logging_out, Style.INFO).show();
        }
    }

    @Override
    public void onFragmentInteraction(Bundle bundle) {
        String action = bundle.getString("action", "none");
        if (action.equals("login")) {

        } else if (action.equals("register")) {
            
        } else if (action.equals("cancel")){

        }

    }

    public void launchAnimatedFragment(Fragment fragment) {

    }

//    public void attemptLogin() {
//        if (mAuthTask != null) { // prevent multiple login attempts
//            return;
//        }
//
//        // Store values at the time of the login attempt.
//        String email = mEmailView.getText().toString();
//        String password = mPasswordView.getText().toString();
//
//        String croutonMessage = "";
//        boolean cancel = false;
//        View focusView = null;
//
//
//        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            croutonMessage = getString(R.string.error_invalid_password);
//            focusView = mPasswordView;
//            cancel = true;
//        }
//
//        // Check for a valid email address.
//        if (TextUtils.isEmpty(email)) {
//            croutonMessage = getString(R.string.error_email_required);
//            focusView = mEmailView;
//            cancel = true;
//        } else if (!isEmailValid(email)) {
//            croutonMessage = getString(R.string.error_invalid_email);
//            focusView = mEmailView;
//            cancel = true;
//        }
//
//        if (cancel) {
//            Crouton.makeText(this, croutonMessage, Style.ALERT).show();
//            focusView.requestFocus();
//        } else {
//            mAuthTask = new UserLoginTask(this, email, password);
//            mAuthTask.execute((Void) null);
//        }
//    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private Activity mActivity;
        private String mEmail;
        private String mPassword;

        UserLoginTask(Activity activity, String email, String password) {
            this.mActivity = activity;
            this.mEmail = email;
            this.mPassword = password;
            mUser = new User();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            mUser = mUser.findUser(mEmail);
            if (mUser != null) {
                User u = new User(mEmail, mPassword, mUser.getSalt());
                if (u.getEmail().equals(mUser.getEmail())) {
                    return u.getPassword().equals(mUser.getPassword());
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("loggedIn", true);
                editor.putString("email", mUser.getEmail());
                editor.apply();

                Intent data = new Intent();
                data.putExtra("user", mUser);
                setResult(RESULT_OK, data);
                finish();
            } else {
                Crouton.makeText(mActivity, R.string.message_login_failed, Style.ALERT).show();
                //mEmailView.setText("");
                //mPasswordView.setText("");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}




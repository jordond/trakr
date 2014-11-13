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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

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
import me.dehoog.trakr.fragments.RegisterFragment;
import me.dehoog.trakr.models.User;
//TODO implement attempLogin() and attemptRegister(), then handle only clicking the button once, don't allow multiple clicks
/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoginFragment.OnFragmentInteractionListener {

    private UserLoginTask mAuthTask = null;
    private User mUser = new User();

    private FragmentTransaction ft;
    private LoginFragment mLoginFragment;
    private RegisterFragment mRegisterFragment;

    // TODO DEBUG CODE remove after
    public void debugLogin() {
        //debug user
        mUser = mUser.findUser("t@t.com");
        if (mUser == null) {
            mUser = new User("t@t.com", "test123");
            mUser.save();
        }
        mAuthTask = new UserLoginTask(this, "t@t.com", "test123");
        mAuthTask.execute((Void) null);
    }
    // DEBUG CODE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getActionBar() != null) {
            getActionBar().hide();
        }

        mLoginFragment = LoginFragment.newInstance();
        mRegisterFragment = RegisterFragment.newInstance();

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
            attemptLogin(bundle.getStringArray("credentials"));
        } else if (action.equals("switch_register")) {
            launchAnimatedFragment(mRegisterFragment);
        } else if (action.equals("create")) {
            attemptRegister(bundle.getStringArray("credentials"));
        } else if (action.equals("switch_login")){
            launchAnimatedFragment(mLoginFragment);
        } else if (action.equals("debug")) {
            debugLogin();
        }
    }

    public void launchAnimatedFragment(Fragment fragment) {
        if (fragment != null) {
            ft = getFragmentManager().beginTransaction();

            ft.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, R.animator.slide_up, R.animator.slide_down)
                    .replace(R.id.container_login, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void attemptLogin(String[] credentials) {
        if (mAuthTask != null) { // prevent multiple login attempts
            return;
        }

        // Store values at the time of the login attempt.
        String email = credentials[0];
        String password = credentials[1];

        String croutonMessage = "";
        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            croutonMessage = getString(R.string.error_password_required);
            focusView = mLoginFragment.getView("password");
            cancel = true;
        } else if (!User.isPasswordValid(password)) {
            croutonMessage = getString(R.string.error_invalid_password);
            focusView = mLoginFragment.getView("password");
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            croutonMessage = getString(R.string.error_email_required);
            focusView = mLoginFragment.getView("email");
            cancel = true;
        } else if (!User.isEmailValid(email)) {
            croutonMessage = getString(R.string.error_invalid_email);
            focusView = mLoginFragment.getView("email");
            cancel = true;
        }

        if (cancel) {
            Crouton.makeText(this, croutonMessage, Style.ALERT).show();
            focusView.requestFocus();
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(focusView);
        } else {
            mAuthTask = new UserLoginTask(this, email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private void attemptRegister(String[] credentials) {
        Crouton.makeText(this, "Username: " + credentials[0] + " Email: " + credentials[1] + " Password: " + credentials[2], Style.CONFIRM).show();
    }

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
                mLoginFragment.clearAllEditTexts();
                mLoginFragment.getView("email").requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}




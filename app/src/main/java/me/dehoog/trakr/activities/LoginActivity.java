package me.dehoog.trakr.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import me.dehoog.trakr.fragments.LoginFragment;
import me.dehoog.trakr.fragments.RegisterFragment;
import me.dehoog.trakr.interfaces.OnTaskResult;
import me.dehoog.trakr.models.User;
import me.dehoog.trakr.tasks.UserLoginTask;
import me.dehoog.trakr.tasks.UserRegisterTask;
//TODO implement attempLogin() and attemptRegister(), then handle only clicking the button once, don't allow multiple clicks
/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoginFragment.OnFragmentInteractionListener {

    private UserLoginTask mLoginTask = null;
    private UserRegisterTask mRegisterTask = null;
    private OnTaskResult mOnTaskResult;
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
        mLoginTask = new UserLoginTask("t@t.com", "test123", mOnTaskResult);
        mLoginTask.execute((Void) null);
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
        mOnTaskResult = new OnTaskResult() {
            @Override
            public void onTaskCompleted(Bundle bundle) {
                taskCompleted(bundle);
            }

            @Override
            public void onTaskCancelled(String action) {
                taskCancelled(action);
            }
        };

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
            if (mLoginTask != null) {
                mLoginTask.cancel(true);
            }
            launchAnimatedFragment(mRegisterFragment);
        } else if (action.equals("create")) {
            attemptRegister(bundle.getStringArray("credentials"));
        } else if (action.equals("switch_login")) {
            if (mRegisterTask != null) {
                mRegisterTask.cancel(true);
            }
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
        if (mLoginTask != null) { // prevent multiple login attempts
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
            mLoginTask = new UserLoginTask(email, password, mOnTaskResult);
            mLoginTask.execute((Void) null);
        }
    }

    private void attemptRegister(String[] credentials) {
        if (mRegisterTask != null) {
            return;
        }
    }

    public void saveUserAndLogin(User user) {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        Intent data = new Intent();

        editor.putBoolean("loggedIn", true);
        editor.putString("email", user.getEmail());
        editor.apply();

        data.putExtra("user", user);
        setResult(RESULT_OK, data);
        finish();
    }

    public void taskCompleted(Bundle bundle) {
        String action = bundle.getString("action", "none");
        boolean success = bundle.getBoolean("success", false);

        if (action.equals("login")) {
            if (success) {
                User user = (User) bundle.getSerializable("user");
                saveUserAndLogin(user);

            } else {
                Crouton.makeText(this, R.string.message_login_failed, Style.ALERT).show();
                mLoginFragment.clearAllEditTexts();
                mLoginFragment.getView("email").requestFocus();
            }
        } else if (action.equals("register")) {
            if (success) {
                User user = (User) bundle.getSerializable("user");
                saveUserAndLogin(user);
            } else {
                Crouton.makeText(this, bundle.getString("message"), Style.ALERT).show();
                mRegisterFragment.clearAllEditTexts();
                mRegisterFragment.getView("email").requestFocus();
            }
        }
    }

    public void taskCancelled(String action) {
        if (action.equals("login")) {
            mLoginTask = null;
        } else if (action.equals("register")) {
            mRegisterTask = null;
        }
    }
}



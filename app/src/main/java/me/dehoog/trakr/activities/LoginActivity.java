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
import me.dehoog.trakr.R;
import me.dehoog.trakr.fragments.LoginFragment;
import me.dehoog.trakr.fragments.RegisterFragment;
import me.dehoog.trakr.helpers.SeedDatabase;
import me.dehoog.trakr.interfaces.OnTaskResult;
import me.dehoog.trakr.models.User;
import me.dehoog.trakr.tasks.UserLoginTask;
import me.dehoog.trakr.tasks.UserRegisterTask;

public class LoginActivity extends Activity implements LoginFragment.OnFragmentInteractionListener {

    public static final String PREFS_NAME = "TrakrPrefs";
    public SharedPreferences mSettings;

    private UserLoginTask mLoginTask = null;
    private UserRegisterTask mRegisterTask = null;
    private OnTaskResult mOnTaskResult;

    private FragmentTransaction ft;
    private LoginFragment mLoginFragment;
    private RegisterFragment mRegisterFragment;

    private User mUser = new User();

    // TODO DEBUG CODE remove after
    public void debugLogin() {
        //debug user
        mUser = mUser.findUser("test0@test.com");
        if (mUser == null) {
            mUser = new User("test0@test.com", "password");
            mUser.setUsername("username0");
            mUser.save();
        }
        mLoginTask = new UserLoginTask("test0@test.com", "password", mOnTaskResult);
        mLoginTask.execute((Void) null);
    }
    // DEBUG CODE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSettings = getSharedPreferences(PREFS_NAME, 0);
        if (getLoggedInState()) {
            mSettings.edit()
                    .putBoolean("loggedIn", true)
                    .putString("email", mUser.getEmail())
                    .apply();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("user", mUser);
            startActivity(intent);
            finish();
        }

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

        boolean loggingOut = mSettings.getBoolean("loggingOut", false);
        if (loggingOut) {
            Crouton.makeText(this, R.string.message_logging_out, Style.INFO).show();
        }

        //TODO DEBUG CODE
//        SeedDatabase seed = new SeedDatabase();
//        if (seed.users())
//            seed.accounts();
    }

    public boolean getLoggedInState() {
        boolean loggedIn = mSettings.getBoolean("loggedIn", false);
        if (loggedIn) {
            mUser = new User().findUser(mSettings.getString("email", "none"));
            if (mUser == null) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onFragmentInteraction(Bundle bundle) {
        String action = bundle.getString("action", "none");
        if (action.equals("login")) {
            if (mLoginTask == null) {
                attemptAction("login", bundle.getStringArray("credentials"));
            }
        } else if (action.equals("switch_register")) {
            if (mLoginTask != null) {
                mLoginTask.cancel(true);
            }
            launchAnimatedFragment(mRegisterFragment);
        } else if (action.equals("create")) {
            if (mRegisterTask == null) {
                attemptAction("register", bundle.getStringArray("credentials"));
            }
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

    public void attemptAction(String action, String[] credentials) {
        if (mLoginTask != null) { // prevent multiple login attempts
            return;
        }

        String username = "", email, password;
        if (action.equals("register")) {
            username = credentials[0];
            email = credentials[1];
            password = credentials[2];
        } else {
            email = credentials[0];
            password = credentials[1];
        }

        String croutonMessage = "";
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            croutonMessage = getString(R.string.error_password_required);
            focusView = findViewById(R.id.login_password);
            cancel = true;
        } else if (!User.isPasswordValid(password)) {
            croutonMessage = getString(R.string.error_invalid_password);
            focusView = findViewById(R.id.login_password);
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            croutonMessage = getString(R.string.error_email_required);
            focusView = findViewById(R.id.login_email);
            cancel = true;
        } else if (!User.isEmailValid(email)) {
            croutonMessage = getString(R.string.error_invalid_email);
            focusView = findViewById(R.id.login_email);
            cancel = true;
        }

        // Check for valid username
        if (action.equals("register")) {
            if (TextUtils.isEmpty(username)) {
                croutonMessage = getString(R.string.error_username_required);
                focusView = findViewById(R.id.register_username);
                cancel = true;
            } else if (!User.isUsernameValid(username)) {
                croutonMessage = getString(R.string.error_username_invalid);
                focusView = findViewById(R.id.register_username);
                cancel = true;
            }
        }


        if (cancel) {
            Crouton.makeText(this, croutonMessage, Style.ALERT).show();
            focusView.requestFocus();
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(focusView);
        } else {
            if (action.equals("login")) {
                mLoginTask = new UserLoginTask(email, password, mOnTaskResult);
                mLoginTask.execute((Void) null);
            } else {
                mRegisterTask = new UserRegisterTask(username, email, password, mOnTaskResult);
                mRegisterTask.execute((Void) null);
            }
        }
    }

    public void saveUserAndLogin(User user) {
        mSettings.edit()
                .putBoolean("loggedIn", true)
                .putString("email", user.getEmail())
                .apply();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("loggingIn", true);
        startActivity(intent);
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
                findViewById(R.id.login_email).requestFocus();
            }
            mLoginTask = null;
        } else if (action.equals("register")) {
            if (success) {
                User user = (User) bundle.getSerializable("user");
                saveUserAndLogin(user);
            } else {
                Crouton.makeText(this, bundle.getString("message"), Style.ALERT).show();
                mRegisterFragment.clearAllEditTexts();
                findViewById(R.id.register_username).requestFocus();
            }
            mRegisterTask = null;
        }

        if (!success) {
            YoYo.with(Techniques.Wobble)
                .duration(500)
                .playOn(findViewById(R.id.ui_container));
        }
    }

    public void taskCancelled(String action) {
        if (action.equals("login")) {
            mLoginTask = null;
        } else if (action.equals("register")) {
            mRegisterTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}



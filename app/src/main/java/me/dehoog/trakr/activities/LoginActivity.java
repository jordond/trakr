package me.dehoog.trakr.activities;

import android.app.Activity;
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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.User;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    private UserLoginTask mAuthTask = null;
    private User mUser = new User();

    // UI references.
    @InjectView(R.id.email) AutoCompleteTextView mEmailView;
    @InjectView(R.id.password) EditText mPasswordView;
    @InjectView(R.id.login_form) View mLoginFormView;
    @InjectView(R.id.debug_login_button) Button mDebugLogin;

    // Butter knife listeners
    @OnClick(R.id.email_sign_in_button) void onClickLogin() {
        attemptLogin();
    }

    // DEBUG CODE
    @OnClick(R.id.debug_login_button) void debugLogin() {
        //debug user
        mUser = mUser.findUser("t@t");
        if (mUser == null) {
            mUser = new User("t@t", "test123");
            mUser.save();
        }
        mAuthTask = new UserLoginTask("t@t", "test123");
        mAuthTask.execute((Void) null);
    }
    // DEBUG CODE

    @OnEditorAction(R.id.password) boolean onEditorAction(int id) {
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            attemptLogin();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this); // all your injects belong to us
        populateAutoComplete();

        boolean loggingOut = getIntent().getExtras().getBoolean("loggingOut");
        if (loggingOut) {
           Crouton.makeText(this, R.string.message_logging_out, Style.INFO).show();
        }
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) { // prevent multiple login attempts
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
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
                mPasswordView.setError(getString(R.string.error_invalid_login));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}




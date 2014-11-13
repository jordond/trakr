package me.dehoog.trakr.tasks;

import android.os.AsyncTask;
import android.os.Bundle;

import me.dehoog.trakr.interfaces.OnTaskResult;
import me.dehoog.trakr.models.User;

/**
 * Author:  jordon
 * Created: November, 13, 2014
 * 1:25 PM
 */
public class UserRegisterTask extends AsyncTask<Void, Void, Bundle> {
    private User mUser;
    private String mUsername;
    private String mEmail;
    private String mPassword;
    private OnTaskResult mListener;

    public UserRegisterTask(String username, String email, String password, OnTaskResult listener) {
        mUser = new User();
        this.mUsername = username;
        this.mEmail = email;
        this.mPassword = password;
        this.mListener = listener;
    }

    @Override
    protected Bundle doInBackground(Void... params) {

        Bundle b = new Bundle();
        String message = "";
        boolean error = false;

        if (mUser.usernameExists(mUsername)) {
            message = "Username already exists";
            error = true;
        }

        if (mUser.emailExists(mEmail)) {
            message = "Email already exists";
            error = true;
        }

        if (!error) {
            User u = new User();
            u.setUsername(mUsername);
            u.setEmail(mEmail);
            u.setPassword(mPassword);
            u.save();

            b.putBoolean("success", true);
            b.putSerializable("user", u);
        } else {
            b.putBoolean("success", false);
            b.putString("message", message);
        }

        return b;
    }

    @Override
    protected void onPostExecute(final Bundle bundle) {
        bundle.putString("action", "register");
        mListener.onTaskCompleted(bundle);
    }

    @Override
    protected void onCancelled() {
        mListener.onTaskCancelled("register");
    }
}

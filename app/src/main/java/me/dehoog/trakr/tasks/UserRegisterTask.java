package me.dehoog.trakr.tasks;

import android.os.AsyncTask;

import me.dehoog.trakr.interfaces.OnTaskResult;
import me.dehoog.trakr.models.User;

/**
 * Author:  jordon
 * Created: November, 13, 2014
 * 1:25 PM
 */
public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
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
    protected Boolean doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        mListener.onTaskCompleted("login", success);
    }

    @Override
    protected void onCancelled() {
        mListener.onTaskCancelled("login");
    }
}

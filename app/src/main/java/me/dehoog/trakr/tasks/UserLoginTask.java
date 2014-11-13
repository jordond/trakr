package me.dehoog.trakr.tasks;

import android.os.AsyncTask;

import me.dehoog.trakr.interfaces.OnTaskResult;
import me.dehoog.trakr.models.User;

/**
 * Author:  jordon
 * Created: November, 13, 2014
 * 1:13 PM
 */
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
    private User mUser;
    private String mEmail;
    private String mPassword;
    private OnTaskResult mListener;

    public UserLoginTask(String email, String password, OnTaskResult listener) {
        mUser = new User();
        this.mEmail = email;
        this.mPassword = password;
        this.mListener = listener;
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
        mListener.onTaskCompleted("login", success);
    }

    @Override
    protected void onCancelled() {
        mListener.onTaskCancelled("login");
        //mAuthTask = null;
    }
}

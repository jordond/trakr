package me.dehoog.trakr.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import me.dehoog.trakr.activities.LoginActivity;
import me.dehoog.trakr.interfaces.OnTaskResult;
import me.dehoog.trakr.models.User;

/**
 * Author:  jordon
 * Created: November, 13, 2014
 * 1:13 PM
 */
public class UserLoginTask extends AsyncTask<Void, Void, User> {

    ProgressDialog mDialog;

    private User mUser;
    private String mEmail;
    private String mPassword;
    private OnTaskResult mListener;

    public UserLoginTask(Context context, String email, String password, OnTaskResult listener) {
        this.mUser = new User();
        this.mEmail = email;
        this.mPassword = password;
        this.mListener = listener;

        this.mDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        this.mDialog.setMessage("Please wait");
        this.mDialog.setCanceledOnTouchOutside(false);
        this.mDialog.setCancelable(false);
        this.mDialog.show();
        super.onPreExecute();
    }

    @Override
    protected User doInBackground(Void... params) {

        boolean isMatch = false;
        mUser = mUser.findUser(mEmail);
        if (mUser != null) {
            User u = new User(mEmail, mPassword, mUser.getSalt());
            if (u.getEmail().equals(mUser.getEmail())) {
                isMatch = u.getPassword().equals(mUser.getPassword());
            }
        }

        if (isMatch) {
            return mUser;
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(final User user) {
        mDialog.dismiss();
        Bundle bundle = new Bundle();
        bundle.putString("action", "login");
        if (user != null) {
            bundle.putBoolean("success", true);
            bundle.putSerializable("user", user);
        } else {
            bundle.putBoolean("success", false);
        }

        mListener.onTaskCompleted(bundle);
    }

    @Override
    protected void onCancelled() {
        mListener.onTaskCancelled("login");
        mDialog = null;
        //mAuthTask = null;
    }
}

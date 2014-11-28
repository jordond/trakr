package me.dehoog.trakr.tasks;

import android.app.ProgressDialog;
import android.content.Context;
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

    private ProgressDialog mDialog;

    private User mUser;
    private String mUsername;
    private String mEmail;
    private String mPassword;
    private OnTaskResult mListener;

    public UserRegisterTask(Context context, String username, String email, String password, OnTaskResult listener) {
        mUser = new User();
        this.mUsername = username;
        this.mEmail = email;
        this.mPassword = password;
        this.mListener = listener;
        mDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        mDialog.setMessage("Please wait");
        mDialog.show();
        super.onPreExecute();
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
            User u = new User(mEmail, mPassword);
            u.setUsername(mUsername);
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
        mDialog.dismiss();
        bundle.putString("action", "register");
        mListener.onTaskCompleted(bundle);
    }

    @Override
    protected void onCancelled() {
        mDialog = null;
        mListener.onTaskCancelled("register");
    }
}

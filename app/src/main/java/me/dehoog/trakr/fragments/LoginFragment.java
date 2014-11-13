package me.dehoog.trakr.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.dehoog.trakr.R;

public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    // UI stuff
    @InjectView(R.id.login_email) EditText userEmail;
    @InjectView(R.id.login_password) EditText userPassword;

    @OnClick({ R.id.action_login, R.id.action_register})
    public void buttonClicked(Button button) {
        Bundle b = new Bundle();
        switch (button.getId()) {
            case R.id.action_register:
                b.putString("action", "register");
                break;
            case R.id.action_login:
                b.putString("action", "login");
                b.putString("email", userEmail.getText().toString());
                b.putString("password", userPassword.getText().toString());
        }
        if (b.getString("action", "none") != "none") {
            sendBundle(b);
        }
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    public void sendBundle(Bundle bundle) {
        if (mListener != null) {
            mListener.onFragmentInteraction(bundle);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Bundle bundle);
    }

}

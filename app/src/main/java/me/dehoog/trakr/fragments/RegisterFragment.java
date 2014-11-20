package me.dehoog.trakr.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.dehoog.trakr.R;

public class RegisterFragment extends Fragment {

    private LoginFragment.OnFragmentInteractionListener mListener;

    // UI stuff
    @InjectView(R.id.register_username) EditText userUsername;
    @InjectView(R.id.login_email) EditText userEmail;
    @InjectView(R.id.login_password) EditText userPassword;

    @OnClick({ R.id.action_create_account, R.id.action_cancel })
    public void buttonClicked(Button button) {
        Bundle b = new Bundle();
        switch (button.getId()) {
            case R.id.action_create_account:
                b.putString("action", "create");
                String[] credentials = new String[3];
                credentials[0] = userUsername.getText().toString();
                credentials[1] = userEmail.getText().toString();
                credentials[2] =  userPassword.getText().toString();
                b.putStringArray("credentials", credentials);
                break;
            case R.id.action_cancel:
                b.putString("action", "switch_login");
        }
        if (!b.getString("action", "none").equals("none")) {
            sendBundle(b);
        }
    }

    public RegisterFragment() {
    }

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
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
            mListener = (LoginFragment.OnFragmentInteractionListener) activity;
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

    public void clearAllEditTexts() {
        if (userUsername != null && userEmail != null && userPassword != null) {
            userUsername.setText("");
            userEmail.setText("");
            userPassword.setText("");
        }
    }

    public void setText(int id, String text) {
        switch (id) {
            case R.id.register_username:
                userUsername.setText(text);
                break;
            case R.id.login_email:
                userEmail.setText(text);
                break;
            case R.id.login_password:
                userPassword.setText(text);
        }
    }

}

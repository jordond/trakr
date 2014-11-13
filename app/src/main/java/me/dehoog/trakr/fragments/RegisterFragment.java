package me.dehoog.trakr.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.dehoog.trakr.R;

public class RegisterFragment extends Fragment {

    private LoginFragment.OnFragmentInteractionListener mListener;

    // UI stuff
    @InjectView(R.id.create_username) EditText registerUsername;
    @InjectView(R.id.create_email) EditText registerEmail;
    @InjectView(R.id.create_password) EditText registerPassword;

    @OnClick({ R.id.action_create_account, R.id.action_cancel })
    public void buttonClicked(Button button) {
        Bundle b = new Bundle();
        switch (button.getId()) {
            case R.id.action_create_account:
                b.putString("action", "create");
                String[] credentials = new String[3];
                credentials[0] = registerUsername.getText().toString();
                credentials[1] = registerEmail.getText().toString();
                credentials[2] =  registerPassword.getText().toString();
                b.putStringArray("credentials", credentials);
                break;
            case R.id.action_cancel:
                b.putString("action", "cancel");
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

}

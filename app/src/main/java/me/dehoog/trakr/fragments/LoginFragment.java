package me.dehoog.trakr.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.dehoog.trakr.R;

public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    // UI stuff
    @InjectView(R.id.login_email) EditText userEmail;
    @InjectView(R.id.login_password) EditText userPassword;

    @OnClick({ R.id.action_login, R.id.action_register })
    public void buttonClicked(Button button) {
        Bundle b = new Bundle();
        switch (button.getId()) {
            case R.id.action_register:
                b.putString("action", "switch_register");
                break;
            case R.id.action_login:
                b.putString("action", "login");
                String[] credentials = new String[2];
                credentials[0] = userEmail.getText().toString();
                credentials[1] =  userPassword.getText().toString();
                b.putStringArray("credentials", credentials);
        }
        if (!b.getString("action", "none").equals("none")) {
            sendBundle(b);
        }
    }

    @OnClick(R.id.login_logo) // TODO remove after testing
    public void debug() {
        Bundle b = new Bundle();
        b.putString("action", "debug");
        sendBundle(b);
    }

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
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

    public View getView(String view) {
        if (view.equals("email")) {
            return userEmail;
        } else if (view.equals("password")) {
            return userPassword;
        }
        return null;
    }

    public void clearAllEditTexts() {
        if (userEmail != null && userPassword != null) {
            userEmail.setText("");
            userPassword.setText("");
        }
    }

    public void setText(int id, String text) {
        switch (id) {
            case R.id.login_email:
                userEmail.setText(text);
                break;
            case R.id.login_password:
                userPassword.setText(text);
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Bundle bundle);
    }

}

package me.dehoog.trakr.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.User;

public class AddAccountFragment extends Fragment {

    private static final String ARG_USER = "user";

    private User mUser;

    // UI - Text fields
    @InjectView(R.id.account_number) EditText mAccountNumber;
    @InjectView(R.id.expires) EditText mExpiry;
    @InjectView(R.id.descriptive_name) EditText mName;

    // UI - Action buttons
    @InjectView(R.id.action_confirm) Button mConfirm;
    @OnClick(R.id.action_confirm)
    public void onConfirm() {

    }

    @OnClick(R.id.action_nfc)
    public void onReadNfcClick() {
    }

    @OnClick(R.id.action_cancel)
    public void onCancel() { getFragmentManager().popBackStackImmediate(); }

    // UI - Account type toggles
    @InjectView(R.id.toggle_group) RadioGroup mToggleGroup;
    @OnClick({ R.id.toggle_cash, R.id.toggle_credit, R.id.toggle_debit })
    public void onToggle(View view) {
        ((RadioGroup)view.getParent()).check(view.getId());
    }

    public static AddAccountFragment newInstance(User user) {
        AddAccountFragment fragment = new AddAccountFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    public AddAccountFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = (User) getArguments().getSerializable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_account, container, false);
        ButterKnife.inject(this, view);

        mToggleGroup.setOnCheckedChangeListener(ToggleListener);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle("Add Account");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle("");
        }
    }

    static final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
            for (int j = 0; j < radioGroup.getChildCount() - 1; j++) {
                final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
                view.setChecked(view.getId() == i);
            }
        }
    };
}

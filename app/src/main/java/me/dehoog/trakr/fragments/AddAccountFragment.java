package me.dehoog.trakr.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.dehoog.trakr.R;
import me.dehoog.trakr.models.User;

public class AddAccountFragment extends Fragment {
    private static final String ARG_USER = "user";

    private User mUser;

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
        return inflater.inflate(R.layout.fragment_add_account, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle("Add Account");
        }
    }


}

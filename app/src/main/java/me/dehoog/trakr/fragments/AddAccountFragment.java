package me.dehoog.trakr.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.User;

public class AddAccountFragment extends Fragment {

    private static final String ARG_USER = "user";

    private User mUser;

    @OnClick(R.id.cancel)
    public void closeFrag() {
        getFragmentManager().popBackStackImmediate();
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
}

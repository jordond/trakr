package me.dehoog.trakr.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.dehoog.trakr.R;
import me.dehoog.trakr.adapters.CheckInListAdapter;
import me.dehoog.trakr.interfaces.CheckInsInteraction;
import me.dehoog.trakr.models.Purchase;
import me.dehoog.trakr.models.User;

public class CheckInsFragment extends Fragment {

    private static final String ARG_USER = "user";

    @InjectView(R.id.check_in_list) ListView mListView;
    private CheckInListAdapter mAdapter;

    private User mUser;
    private List<Purchase> mCheckIns;

    private CheckInsInteraction mListener;

    public static CheckInsFragment newInstance(User user) {
        CheckInsFragment fragment = new CheckInsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    public CheckInsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_ins, container, false);
        if (getArguments() != null) {
            mUser = (User) getArguments().getSerializable(ARG_USER);
        }

        ButterKnife.inject(this, view);

        mAdapter = new CheckInListAdapter(getActivity());
        setupList();

        return view;
    }

    public void setupList() {
        mCheckIns = mUser.getAllPurchases();
        if (mCheckIns != null && mCheckIns.size() != 0) {
            setupAdapter();
            mListView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.button_new_checkin);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCheckInsInteraction();
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (CheckInsInteraction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CheckInsInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setupAdapter() {
        Collections.sort(mCheckIns);
        Collections.reverse(mCheckIns);
        List<Purchase> group = new ArrayList<Purchase>();
        for (Purchase purchase : mCheckIns) {
            if (group.isEmpty()) {
                group.add(purchase);
            } else {
                if (compare(group.get(0), purchase)) { // check for same day
                    group.add(purchase);
                } else {
                    mAdapter.addCheckIn(group);
                    group.clear();
                    group.add(purchase);
                }
            }
        }
        if (!group.isEmpty()) {
            mAdapter.addCheckIn(group); // add the remaining items to adapter
            group.clear();
        }
        System.out.println("debug");
    }

    private boolean compare(Purchase lhs, Purchase rhs) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lhs.getDate());

        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(rhs.getDate());
        if (year == calendar.get(Calendar.YEAR)) {
            if (day == calendar.get(Calendar.DAY_OF_YEAR)) {
                return true; // same day
            }
        }
        return false;
    }

}

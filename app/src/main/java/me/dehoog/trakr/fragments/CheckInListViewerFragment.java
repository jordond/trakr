package me.dehoog.trakr.fragments;


import android.media.audiofx.AcousticEchoCanceler;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.Purchase;

public class CheckInListViewerFragment extends Fragment {

    private static final String ARG_TYPE = "type";

    private String mType;
    private Account mAccount;
    private List<Purchase> mCheckIns;

    @InjectView(R.id.check_in_list) ListView mListView;
    private CheckInListAdapter mAdapter;

    public static CheckInListViewerFragment newInstance(String type) {
        CheckInListViewerFragment fragment = new CheckInListViewerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    public CheckInListViewerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_in_list_viewer, container, false);

        ButterKnife.inject(this, view);

        if (mType.equals("account")) {
            mCheckIns = mAccount.getPurchases();
            if (mCheckIns.isEmpty()) {
                mCheckIns = mAccount.getAllPurchases();
            }
        } else if (mType.equals("category")) {

        }
        mAdapter = new CheckInListAdapter(getActivity());
        setupList();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.action_close);
        fab.attachToListView(mListView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
    }

    public void setupList() {
        if (mCheckIns != null && mCheckIns.size() != 0) {
            setupAdapter();
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mAdapter.getType(position) == CheckInListAdapter.TYPE_ITEM) {
                        Purchase p = mAdapter.getPurchase(position);
                        CheckInViewerFragment fragment = CheckInViewerFragment.newInstance(p);
                        FragmentTransaction ft  = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_down, R.animator.slide_in_up, R.animator.slide_out_down);
                        ft.replace(R.id.container, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            });
        }
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

    public void setmAccount(Account mAccount) {
        this.mAccount = mAccount;
    }
}

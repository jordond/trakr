package me.dehoog.trakr.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardListView;
import me.dehoog.trakr.R;
import me.dehoog.trakr.adapters.AccountCardArrayAdapter;
import me.dehoog.trakr.cards.AccountCard;
import me.dehoog.trakr.interfaces.AccountsInteraction;
import me.dehoog.trakr.interfaces.EditAccountCallback;
import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.User;

public class AccountsFragment extends Fragment {

    // UI Components
    @InjectView(R.id.cardlist_accounts) CardListView mCardList;

    private static final String ARG_USER = "user";

    private User mUser;
    private AccountsInteraction mListener;
    private Activity mParentActivity;

    public static AccountsFragment newInstance(User user) {
        AccountsFragment fragment = new AccountsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    public AccountsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        if (getArguments() != null) {
            mUser = (User) getArguments().getSerializable(ARG_USER);
        }

        //TODO REMOVE DEBUG CODE
//        Account.deleteAll(Account.class);
//        SeedDatabase seed = new SeedDatabase();
//        seed.accounts();

        ButterKnife.inject(this, view);

        TextView empty = (TextView) view.findViewById(R.id.empty_list);
        empty.setVisibility(View.VISIBLE);
        mCardList.setEmptyView(empty);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mUser.setAccounts(Account.find(Account.class, "user = ?", String.valueOf(mUser.getId())));
        createCards();

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.button_floating_action);
        fab.attachToListView(mCardList);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onAccountsInteraction("add", null);
                }
            }
        });
    }

    public void createCards() {
        ArrayList<Card> cards = new ArrayList<Card>();
        for (Account a : mUser.getAccounts()) {
            AccountCard card = new AccountCard(getActivity())
                    .createExpandCard(a, mParentActivity);

            card.setmListener((EditAccountCallback) mParentActivity);
            card.setCardElevation(getResources().getDimension(R.dimen.cardview_default_elevation));
            card.setShadow(true);
            if (a.getCategory().equals("Cash")) {
                card.setType(2);
            }

            card.setCardElevation(1);

            cards.add(card);
        }

        AccountCardArrayAdapter cardArrayAdapter = new AccountCardArrayAdapter(getActivity(), cards);
        if (mCardList != null) {
            mCardList.setAdapter(cardArrayAdapter);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AccountsInteraction) activity;
            mParentActivity = activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AccountsInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
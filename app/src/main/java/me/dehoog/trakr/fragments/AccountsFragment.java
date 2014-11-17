package me.dehoog.trakr.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardViewNative;
import it.gmariotti.cardslib.library.view.listener.SwipeOnScrollListener;
import me.dehoog.trakr.R;
import me.dehoog.trakr.cards.AccountCard;
import me.dehoog.trakr.helpers.SeedDatabase;
import me.dehoog.trakr.interfaces.AccountsInteraction;
import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.Purchase;
import me.dehoog.trakr.models.User;

public class AccountsFragment extends Fragment {

    // UI Components
    @InjectView(R.id.cardlist_accounts) CardListView mCardList;

    private static final String ARG_USER = "user";

    // TODO: Rename and change types of parameters
    private User mUser;

    private AccountsInteraction mListener;

    // TODO: Rename and change types and number of parameters
    public static AccountsFragment newInstance(User user) {
        AccountsFragment fragment = new AccountsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    public AccountsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        if (getArguments() != null) {
            mUser = (User) getArguments().getSerializable(ARG_USER);
        }

        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mUser.setAccounts(Account.find(Account.class, "user = ?", String.valueOf(mUser.getId())));
        createCards();

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.button_floating_action);
        fab.attachToListView(mCardList);
    }

    public void createCards() {
        ArrayList<Card> cards = new ArrayList<Card>();
        for (Account a : mUser.getAccounts()) {
            AccountCard card = new AccountCard(getActivity())
                    .createExpandCard(a);
            cards.add(card);
        }

        CardArrayAdapter cardArrayAdapter = new CardArrayAdapter(getActivity(), cards);
        if (mCardList != null) {
            mCardList.setAdapter(cardArrayAdapter);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAccountsInteraction(-1);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (AccountsInteraction) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
package me.dehoog.trakr.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.view.CardViewNative;
import me.dehoog.trakr.R;
import me.dehoog.trakr.cards.CategoryListCard;
import me.dehoog.trakr.cards.AccountListCard;
import me.dehoog.trakr.interfaces.SpendingInteraction;
import me.dehoog.trakr.models.User;

public class SpendingFragment extends Fragment {

    private static final String ARG_USER = "user";

    @InjectView(R.id.accounts_card) CardViewNative mAccountsCardView;
    @InjectView(R.id.categories_card) CardViewNative mCategoriesCardView;

    private User mUser;

    private SpendingInteraction mListener;

    public static SpendingFragment newInstance(User user) {
        SpendingFragment fragment = new SpendingFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    public SpendingFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = (User) getArguments().getSerializable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_spending, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initCard();
    }

    private void initCard() {
        AccountListCard mAccountCard = new AccountListCard(getActivity(), mUser);
        mAccountCard.init(); // not really needed, havent decided if i want swipe events
        mAccountsCardView.setCard(mAccountCard);

        CategoryListCard mCategoryCard = new CategoryListCard(getActivity(), mUser);
        mCategoryCard.init();
        mCategoriesCardView.setCard(mCategoryCard);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSpendingInteraction();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (SpendingInteraction) activity;
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

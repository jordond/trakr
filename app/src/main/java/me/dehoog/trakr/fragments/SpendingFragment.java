package me.dehoog.trakr.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.view.CardViewNative;
import me.dehoog.trakr.R;
import me.dehoog.trakr.cards.CategoryListCard;
import me.dehoog.trakr.cards.AccountListCard;
import me.dehoog.trakr.interfaces.SpendingInteraction;
import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.Category;
import me.dehoog.trakr.models.CategoryInformation;
import me.dehoog.trakr.models.Purchase;
import me.dehoog.trakr.models.User;

public class SpendingFragment extends Fragment {

    private static final String ARG_USER = "user";

    @InjectView(R.id.accounts_card) CardViewNative mAccountsCardView;
    @InjectView(R.id.categories_card) CardViewNative mCategoriesCardView;

    private User mUser;
    private List<Category> mCategories;

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

        mCategories = mUser.getCategories();

        initCard();
    }

    double grandTotal = 0.0;
    private void initCard() {
        AccountListCard mAccountCard = new AccountListCard(getActivity(), mUser);
        mAccountCard.init();
        mAccountCard.setOnAccountItemClicked(new AccountListCard.AccountItemClicked() {
            @Override
            public void itemClicked(AccountListCard.AccountObject accountObject) {
                Account account = new Account().findByDescription(accountObject.name);
                if (account != null) {
                    CheckInListViewerFragment fragment = CheckInListViewerFragment.newInstance("account");
                    fragment.setmAccount(account);
                    FragmentTransaction ft  = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_down, R.animator.slide_in_up, R.animator.slide_out_down);
                    ft.replace(R.id.container, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });
        mAccountsCardView.setCard(mAccountCard);


        final List<CategoryInformation> categories = new ArrayList<CategoryInformation>();
        for (Category category : mCategories) {
            CategoryInformation ci = new CategoryInformation();

            for (Purchase p : mUser.getAllPurchases(category.getIconName())) {
                ci.total += p.getAmount();
            }
            ci.category = category.getIconName();
            ci.iconUrl = category.getIcon();
            ci.subCategory = category.getDescription();

            grandTotal += ci.total;
            categories.add(ci);
        }

        CategoryListCard mCategoryCard = new CategoryListCard(getActivity(), categories);
        mCategoryCard.init();
        mCategoryCard.setOnCategoryItemClicked(new CategoryListCard.OnCategoryItemClicked() {
            @Override
            public void itemClicked(int position) {
                CategoryPieFragment pieFragment = CategoryPieFragment.newInstance(grandTotal);
                pieFragment.setCategories(categories);
                pieFragment.setIndex(position);
                launchPie(pieFragment);
            }

            @Override
            public void pieClicked() {
                CategoryPieFragment pieFragment = CategoryPieFragment.newInstance(grandTotal);
                pieFragment.setCategories(categories);
                launchPie(pieFragment);
            }
        });
        mCategoriesCardView.setCard(mCategoryCard);
    }

    public void launchPie(CategoryPieFragment fragment) {
        FragmentTransaction ft  = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_down, R.animator.slide_in_up, R.animator.slide_out_down);
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}

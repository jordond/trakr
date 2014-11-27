package me.dehoog.trakr.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.view.CardViewNative;
import me.dehoog.trakr.R;
import me.dehoog.trakr.cards.CategoryListCard;
import me.dehoog.trakr.cards.AccountListCard;
import me.dehoog.trakr.interfaces.SpendingInteraction;
import me.dehoog.trakr.models.Category;
import me.dehoog.trakr.models.Purchase;
import me.dehoog.trakr.models.User;

public class SpendingFragment extends Fragment {

    private static final String ARG_USER = "user";

    @InjectView(R.id.accounts_card) CardViewNative mAccountsCardView;
    @InjectView(R.id.categories_card) CardViewNative mCategoriesCardView;
    @InjectView(R.id.piechart) PieChart mPieChart;

    private User mUser;
    private List<Category> mCategories;

    private SpendingInteraction mListener;

    private String[] mColors = { "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#03A9F4", "#00BCD4",
            "#009688", "#4CAF50", "#795548", "#607D8B", "#59ABE3", "#FFA400", "#006442", "#264348", "#317589", "#8E44AD", "#F62459" };

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

    private void initCard() {
        AccountListCard mAccountCard = new AccountListCard(getActivity(), mUser);
        mAccountCard.init(); // not really needed, havent decided if i want swipe events
        mAccountsCardView.setCard(mAccountCard);

        double grandTotal = 0.0;
        List<CategoryInformation> categories = new ArrayList<CategoryInformation>();
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

        CategoryListCard mCategoryCard = new CategoryListCard(getActivity(), mUser, categories);
        mCategoryCard.init();
        mCategoriesCardView.setCard(mCategoryCard);

        //setupPieChart(grandTotal, categories);
    }

    private void setupPieChart(double grandTotal, List<CategoryInformation> cats) {
        HashSet<String> usedColors = new HashSet<String>();
        for (CategoryInformation category : cats) {
            int size = usedColors.size();
            String color = getRandomColor();
            usedColors.add(color);
            while (usedColors.size() != size) {
                color = getRandomColor();
                usedColors.add(color);
            }
            double percent = ( category.total / grandTotal ) * 100;
            PieModel slice = new PieModel(category.category, (float) percent, Color.parseColor(color));
            mPieChart.addPieSlice(slice);
        }
        mPieChart.startAnimation();
    }

    private String getRandomColor() {
        int idx = new Random().nextInt(mColors.length);
        return mColors[idx];
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

    public class CategoryInformation {
        public String category;
        public double total;
        public String subCategory;
        public String iconUrl;

        public CategoryInformation() {
        }

        public String getCategory() {
            return category;
        }

        public double getTotal() {
            return total;
        }

        public String getSubCategory() {
            return subCategory;
        }

        public String getIconUrl() {
            return iconUrl;
        }
    }

}

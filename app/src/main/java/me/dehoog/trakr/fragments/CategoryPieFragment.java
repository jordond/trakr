package me.dehoog.trakr.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.CategoryInformation;

public class CategoryPieFragment extends Fragment {
    private static final String ARG_TOTAL = "total";

    private double mTotal;
    private List<CategoryInformation> mCategories;
    private int mIndex;

    @InjectView(R.id.piechart) PieChart mPieChart;
    @OnClick(R.id.close_pie)
    public void close() {
        getFragmentManager().popBackStackImmediate();
    }

    private String[] mColors = { "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#03A9F4", "#00BCD4",
            "#009688", "#4CAF50", "#795548", "#607D8B", "#59ABE3", "#FFA400", "#006442", "#264348", "#317589", "#8E44AD", "#F62459" };

    public static CategoryPieFragment newInstance(double total) {
        CategoryPieFragment fragment = new CategoryPieFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_TOTAL, total);
        fragment.setArguments(args);
        return fragment;
    }

    public CategoryPieFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTotal = getArguments().getDouble(ARG_TOTAL);
            //mIndex = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_pie, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupPieChart();
    }

    public void setCategories(List<CategoryInformation> mCategories) {
        this.mCategories = mCategories;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mPieChart != null) {
                    if (mIndex > 0) {
                        mPieChart.setCurrentItem(mIndex);
                    }
                    mPieChart.startAnimation();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        return anim;
    }

    private void setupPieChart() {
        HashSet<String> usedColors = new HashSet<String>();
        for (CategoryInformation category : mCategories) {
            int size = usedColors.size();
            String color = getRandomColor();
            usedColors.add(color);
            while (usedColors.size() == size) {
                color = getRandomColor();
                usedColors.add(color);
            }
            double percent = ( category.total / mTotal ) * 100;
            PieModel slice = new PieModel(category.category, (float) percent, Color.parseColor(color));
            mPieChart.addPieSlice(slice);
        }
    }

    private String getRandomColor() {
        int idx = new Random().nextInt(mColors.length);
        return mColors[idx];
    }
}

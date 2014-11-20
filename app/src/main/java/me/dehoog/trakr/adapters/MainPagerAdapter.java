package me.dehoog.trakr.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.dehoog.trakr.fragments.AccountsFragment;
import me.dehoog.trakr.fragments.RecentTransactionsFragment;
import me.dehoog.trakr.fragments.SpendingFragment;
import me.dehoog.trakr.models.User;

/**
 * Author:  jordon
 * Created: November, 14, 2014
 * 2:57 PM
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = { "Accounts", "Recent Transactions", "Spending"};

    private User mUser;

    public MainPagerAdapter(FragmentManager fm, User user) {
        super(fm);
        this.mUser = user;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int i) {

        Fragment f = new Fragment();
        switch (i) {
            case 0:
                return AccountsFragment.newInstance(mUser);
            case 1:
                return RecentTransactionsFragment.newInstance("");
            case 2:
                return SpendingFragment.newInstance("");
        }
        return f;
    }

    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }
}

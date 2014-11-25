package me.dehoog.trakr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import me.dehoog.trakr.models.Purchase;

/**
 * Author:  jordon
 * Created: November, 24, 2014
 * 11:09 PM
 */
public class CheckInListAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<Purchase> mCheckIns = new ArrayList<Purchase>();
    private ArrayList<String> mHeaders = new ArrayList<String>();

    private LayoutInflater mInflater;

    public CheckInListAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addCheckIn(final List<Purchase> checkIns) {
        for (Purchase p : checkIns) {
            mCheckIns.add(p);
        }
        notifyDataSetChanged();
    }

    public void addHeader(final String item) {

    }



    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}

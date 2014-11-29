package me.dehoog.trakr.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import me.dehoog.trakr.R;
import me.dehoog.trakr.models.Purchase;

/**
 * Author:  jordon
 * Created: November, 28, 2014
 * 6:14 PM
 */
public class ImportAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Purchase> mPurchases;

    private DecimalFormat mDecimalFormat;

    public ImportAdapter(Context context, List<Purchase> mPurchases) {
        this.mPurchases = mPurchases;
        this.mInflater = ((Activity) context).getLayoutInflater();
        mDecimalFormat = new DecimalFormat("$###,###,###.00");
    }

    @Override
    public int getCount() {
        return mPurchases.size();
    }

    @Override
    public Object getItem(int position) {
        return mPurchases.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.check_in_list_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.merchant_name);
            viewHolder.subtitle = (TextView) convertView.findViewById(R.id.merchant_address);
            viewHolder.amount = (TextView) convertView.findViewById(R.id.check_in_amount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Purchase p = mPurchases.get(position);
        viewHolder.title.setText(p.getMerchant().getName());
        viewHolder.subtitle.setVisibility(View.GONE);
        viewHolder.amount.setText(mDecimalFormat.format(p.getAmount()));

        return convertView;
    }

    public static class ViewHolder {
        public TextView title;
        public TextView subtitle;
        public TextView amount;
    }

}

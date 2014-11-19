package me.dehoog.trakr.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import me.dehoog.trakr.R;
import me.dehoog.trakr.models.Purchase;

/**
 * Author:  jordon
 * Created: November, 16, 2014
 * 4:42 PM
 */
public class RecentTransactionAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Purchase> mPurchases;

    public RecentTransactionAdapter(Context context, List<Purchase> purchases) {
        this.mContext = context;
        this.mPurchases = purchases;
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

        if (mInflater == null) {
            mInflater = ((Activity) mContext).getLayoutInflater();
        }
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.card_list_transactions, parent, false);
        }

        TextView tDate = (TextView) convertView.findViewById(R.id.list_transaction_date);
        TextView tMerchant = (TextView) convertView.findViewById(R.id.list_transaction_merchant);
        TextView tAmount = (TextView) convertView.findViewById(R.id.list_transaction_amount);

        Purchase p = mPurchases.get(position);
        String date = new SimpleDateFormat("MMM dd h:mma").format(p.getDate()).toString();

        tDate.setText(date);
        tMerchant.setText(p.getMerchant().getName().toString());

        String amount = new DecimalFormat("$###,###,###.00").format(p.getAmount());
        tAmount.setText(amount);

        return convertView;
    }

}

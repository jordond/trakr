package me.dehoog.trakr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.dehoog.trakr.R;
import me.dehoog.trakr.models.Merchant;
import me.dehoog.trakr.models.Purchase;

/**
 * Author:  jordon
 * Created: November, 24, 2014
 * 11:09 PM
 */
public class CheckInListAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<Integer> mTypes = new ArrayList<Integer>();
    private ArrayList<Purchase> mCheckIns = new ArrayList<Purchase>();
    private ArrayList<String> mHeaders = new ArrayList<String>();
    private ArrayList<Integer> mHeaderIndex = new ArrayList<Integer>();

    private LayoutInflater mInflater;

    private SimpleDateFormat mFormat;
    private DecimalFormat mDecimalFormat;

    public CheckInListAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFormat = new SimpleDateFormat("MMMM d, yyyy");
        mDecimalFormat = new DecimalFormat("$###,###,###.00");
    }

    public void addHeader(final Date item) {
        mTypes.add(TYPE_SEPARATOR);
        mHeaders.add(mFormat.format(item));
        mCheckIns.add(new Purchase()); // dummy
    }

    public void addCheckIn(final List<Purchase> checkIns) {
        addHeader(checkIns.get(0).getDate());
        for (Purchase p : checkIns) {
            mTypes.add(TYPE_ITEM);
            mHeaders.add("filler");
            mCheckIns.add(p);
        }
        notifyDataSetChanged();
    }

    public Purchase getPurchase(int position) {
        return mCheckIns.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mTypes.get(position);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mCheckIns.size();
    }

    @Override
    public Object getItem(int position) {
        return mCheckIns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        int rowType = getItemViewType(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (rowType == TYPE_SEPARATOR) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.check_in_list_header, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(R.id.check_in_date);
            } else {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.check_in_list_item, parent, false);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.merchant_icon);
                viewHolder.title = (TextView) convertView.findViewById(R.id.merchant_name);
                viewHolder.subtitle = (TextView) convertView.findViewById(R.id.merchant_address);
                viewHolder.extra = (TextView) convertView.findViewById(R.id.check_in_amount);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (rowType == TYPE_SEPARATOR) {
            viewHolder.title.setText(mHeaders.get(position));
        } else {
            Purchase p = mCheckIns.get(position);
            Merchant m = p.getMerchant();
            String accountType = p.getAccount().getCategory().toLowerCase();
            viewHolder.icon.setImageResource(viewHolder.getIconResourse(accountType));
            viewHolder.title.setText(m.getName());
            viewHolder.subtitle.setText(m.getLocation().getLongAddress());
            viewHolder.extra.setText(mDecimalFormat.format(p.getAmount()));
        }
        return convertView;
    }

    public static class ViewHolder {
        public ImageView icon;
        public TextView title;
        public TextView subtitle;
        public TextView extra;

        public int getIconResourse(String type) {
            if (type.equals("cash")) {
                return R.drawable.ic_cash;
            } else if (type.equals("credit")) {
                return R.drawable.ic_credit;
            } else {
                return R.drawable.ic_debit;
            }
        }
    }
}

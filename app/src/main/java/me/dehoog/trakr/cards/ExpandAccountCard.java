package me.dehoog.trakr.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardExpand;
import me.dehoog.trakr.R;
import me.dehoog.trakr.adapters.RecentCheckInsAdapter;
import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.Purchase;

/**
 * Author:  jordon
 * Created: November, 16, 2014
 * 12:18 AM
 */
public class ExpandAccountCard extends CardExpand {

    private Account mAccount;
    private List<Purchase> mTransactions;

    private Context mContext;
    private ExpandListClick mListener;

    public ExpandAccountCard(Context context, Account account) {
        super(context, R.layout.card_account_expand_transaction_list);
        this.mAccount = account;
        this.mContext = context;
    }

    public ExpandAccountCard(Context context, int innerLayout) {
        super(context, R.layout.card_account_expand_transaction_list);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view == null) {
            return;
        }

        //TODO add item click event on transactions list to show the current transaction
        ListView listView = (ListView) view.findViewById(R.id.card_account_expand_transaction_list);

        mTransactions = mAccount.getAllPurchases();
        int numTransactions = mTransactions.size();
        if (mTransactions.isEmpty()) {
            TextView recentTransHeader = (TextView) view.findViewById(R.id.recent_transaction_header);
            recentTransHeader.setText("No check in\'s");
        }
        if (numTransactions > 3) {
            mTransactions = mTransactions.subList(numTransactions - 3, numTransactions);
        }

        RecentCheckInsAdapter adapter = new RecentCheckInsAdapter(mContext, mTransactions);
        listView.setAdapter(adapter);
        listView.setDivider(mContext.getResources().getDrawable(R.drawable.transperent_color));
        listView.setDividerHeight(0);
        setListViewHeight(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null) {
                    Purchase purchase = mTransactions.get(position);
                    mListener.expandListItemClicked(purchase);
                }
            }
        });

        TextView totalTransactions = (TextView) view.findViewById(R.id.total_transactions_num);
        totalTransactions.setText(String.valueOf(numTransactions));

        TextView totalAmount = (TextView) view.findViewById(R.id.total_spent_text);
        String total = new DecimalFormat("$###,###,###.00").format(mAccount.getTotal());
        if (total.equals("$.00")) {
            total = "zero";
        }
        totalAmount.setText(total);
    }

    public void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void setmListener(ExpandListClick mListener) {
        this.mListener = mListener;
    }

    public interface ExpandListClick {
        public void expandListItemClicked(Purchase purchase);
    }

}

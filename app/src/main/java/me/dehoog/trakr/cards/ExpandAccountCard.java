package me.dehoog.trakr.cards;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardExpand;
import me.dehoog.trakr.R;
import me.dehoog.trakr.adapters.RecentTransactionAdapter;
import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.Purchase;

/**
 * Author:  jordon
 * Created: November, 16, 2014
 * 12:18 AM
 */
public class ExpandAccountCard extends CardExpand {

    private Account mAccount;
    private Context mContext;

    public ExpandAccountCard(Context context, Account account) {
        super(context, R.layout.card_account_expand_transaction_list);
        this.mAccount = account;
        this.mContext = context;
    }

    public ExpandAccountCard(Context context, int innerLayout) {
        super(context, R.layout.card_account_expand);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view == null) {
            return;
        }

        //TODO add item click event on transactions list to show the current transaction
        //TODO add something when there is no transactions
        ListView listView = (ListView) view.findViewById(R.id.card_account_expand_transaction_list);
        List<Purchase> transactions = mAccount.getAllPurchases();
        if (transactions.isEmpty()) {
            TextView recentTransHeader = (TextView) view.findViewById(R.id.recent_transaction_header);
            recentTransHeader.setText("no transactions");
        }
        if (transactions.size() > 3) {
            transactions = transactions.subList(0,3);
        }
        RecentTransactionAdapter adapter = new RecentTransactionAdapter(mContext, transactions);
        listView.setAdapter(adapter);
        listView.setDivider(mContext.getResources().getDrawable(R.drawable.transperent_color));
        listView.setDividerHeight(0);
        setListViewHeight(listView);

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
}

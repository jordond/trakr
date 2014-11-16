package me.dehoog.trakr.cards;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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

        // TODO setup the expanded card elements
        ListView listView = (ListView) view.findViewById(R.id.card_account_expand_transaction_list);
        List<Purchase> transactions = mAccount.getAllPurchases();
        if (transactions.size() > 3) {
            transactions = transactions.subList(0,3);
        }
        RecentTransactionAdapter adapter = new RecentTransactionAdapter(mContext, transactions);
        listView.setAdapter(adapter);
        listView.setDivider(mContext.getResources().getDrawable(R.drawable.transperent_color));
        listView.setDividerHeight(0);

        TextView totalAmount = (TextView) view.findViewById(R.id.total_spent_text);
        String total = new DecimalFormat("$###,###,###.00").format(mAccount.getTotal());
        if (total.equals("$.00")) {
            total = "zero";
        }
        totalAmount.setText(total);
    }
}

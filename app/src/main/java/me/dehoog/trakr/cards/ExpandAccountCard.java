package me.dehoog.trakr.cards;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

        RecentTransactionAdapter adapter = new RecentTransactionAdapter(mContext, transactions);
        listView.setAdapter(adapter);
    }
}

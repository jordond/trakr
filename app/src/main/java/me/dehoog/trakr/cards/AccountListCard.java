package me.dehoog.trakr.cards;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.User;

/**
 * Author:  jordon
 * Created: November, 26, 2014
 * 2:21 PM
 */
public class AccountListCard extends CardWithList {

    private User mUser;
    private List<Account> mAccounts;

    public AccountListCard(Context context, User user) {
        super(context);
        this.mUser = user;
    }

    @Override
    protected CardHeader initCardHeader() {

//        CardHeader header = new AccountListCardHeader(getContext(), R.layout.account_list_card_inner_header, "Accounts");
//        header.setPopupMenu(R.menu.card_popup, new CardHeader.OnClickCardHeaderPopupMenuListener() {
//            @Override
//            public void onMenuItemClick(BaseCard baseCard, MenuItem menuItem) {
//
//            }
//        });
        CardHeader header = new AccountListCardHeader(getContext(), R.layout.account_list_card_inner_header, "Accounts");
        return header;
    }

    @Override
    protected void initCard() {
    }

    @Override
    protected List<ListObject> initChildren() {

        DecimalFormat decimalFormat = new DecimalFormat("$###,###,###.00");

        List<Account> accounts = mUser.getAllAccounts();
        List<ListObject> children = new ArrayList<ListObject>();

        for (Account account : accounts) {
            if (account.getTotal() == 0) {
                continue;
            }
            AccountObject ao = new AccountObject(this);
            ao.name = account.getDescription();

            double total = account.getTotal();
            if (total > 0) {
                ao.total = String.valueOf(decimalFormat.format(account.getTotal()));
            } else {
                ao.total = "Zero";
            }
            ao.category = account.getCategory();

            String category = account.getCategory();
            if (category.equals("Cash")) {
                ao.categoryIcon = R.drawable.ic_cash;
            } else if (category.equals("Debit")) {
                ao.categoryIcon = R.drawable.ic_debit;
            } else {
                ao.categoryIcon = R.drawable.ic_credit;
            }
            children.add(ao);
        }
        if (children.isEmpty()) {
            return null;
        } else {
            return children;
        }
    }

    @Override
    public View setupChildView(int i, ListObject listObject, View view, ViewGroup viewGroup) {
        TextView name = (TextView) view.findViewById(R.id.account_list_card_inner_title);
        TextView total = (TextView) view.findViewById(R.id.account_list_card_inner_ammount);

        AccountObject obj = (AccountObject) listObject;
        name.setText(obj.name);
        name.setCompoundDrawablesWithIntrinsicBounds(obj.categoryIcon, 0, 0, 0);
        total.setText(obj.total);

        return view;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.account_list_card_inner_main;
    }

    public class AccountObject extends DefaultListObject {
        public String name;
        public int categoryIcon;
        public String category;
        public String total;

        public AccountObject(Card parentCard) {
            super(parentCard);
        }
    }

    public class AccountListCardHeader extends CardHeader {

        private String mTitle;

        public AccountListCardHeader(Context context, int innerLayout, String title) {
            super(context, innerLayout);
            this.mTitle = title;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            super.setupInnerViewElements(parent, view);

            TextView titleView = (TextView) view.findViewById(R.id.text_birth1);
            if (titleView != null) {
                titleView.setText(mTitle);
            }
        }

    }

}

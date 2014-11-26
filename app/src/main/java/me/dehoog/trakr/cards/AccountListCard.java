package me.dehoog.trakr.cards;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

/**
 * Author:  jordon
 * Created: November, 26, 2014
 * 2:21 PM
 */
public class AccountListCard extends CardWithList {

    public AccountListCard(Context context) {
        super(context);
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

        List<ListObject> objects = new ArrayList<ListObject>();
        AccountObject a1 = new AccountObject(this);
        a1.name = "Sample Account";
        a1.category = "Visa";
        a1.categoryIcon = R.drawable.ic_credit;
        a1.total = "$453.33";
        objects.add(a1);

        AccountObject a2 = new AccountObject(this);
        a2.name = "Sample Debit";
        a2.category = "Debit";
        a2.categoryIcon = R.drawable.ic_debit;
        a2.total = "$14.33";
        objects.add(a2);

        AccountObject a3 = new AccountObject(this);
        a3.name = "Sample Cash";
        a3.category = "Cash";
        a3.categoryIcon = R.drawable.ic_cash;
        a3.total = "$87.11";
        objects.add(a3);

        return objects;
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
            init();
        }

        private void init() {

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

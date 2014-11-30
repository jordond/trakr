package me.dehoog.trakr.cards;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.base.CardViewWrapper;
import it.gmariotti.cardslib.library.view.component.CardThumbnailView;
import me.dehoog.trakr.R;
import me.dehoog.trakr.interfaces.EditAccountCallback;
import me.dehoog.trakr.models.Account;

/**
 * Author:  jordon
 * Created: November, 15, 2014
 * 11:58 PM
 *
 * One of my least favourite things in the world, this class, and any class relating to the cards view
 * enjoy the tangled web that is the CardsLib.
 *
 **/
public class AccountCard extends Card {

    protected Account mAccount;
    private EditAccountCallback mListener;

    public AccountCard(Context context, Account account) {
        super(context, R.layout.card_account);
        this.mAccount = account;
        init();
    }

    public AccountCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public AccountCard(Context context) {
        super(context, R.layout.card_account);
    }

    private void init() {

        CardHeader header = new AccountCardHeader(getContext(), R.layout.card_account_header, mAccount.getDescription());
        header.setTitle("Not Visible");
        header.setButtonExpandVisible(true);

        CardThumbnail icon = new CardThumbnail(getContext());
        int iconId = getIcon(mAccount.getCategory());
        if (iconId != -1) {
            icon.setDrawableResource(getIcon(mAccount.getCategory()));
        }

        addCardHeader(header);
        addCardThumbnail(icon);
        setShadow(false);

        OnCardClickListener clickListener = new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                card.doToogleExpand();
            }
        };
        addPartialOnClickListener(Card.CLICK_LISTENER_ALL_VIEW,clickListener);

    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (!mAccount.isValid()) {  // fix crashing check if account is valid
            return;
        }

        TextView sub = (TextView) view.findViewById(R.id.card_inner_subtitle);

        if (mAccount.getCategory().toLowerCase().equals("cash")) {
            sub.setText(mAccount.getCategory());
        } else {
            TextView main = (TextView) view.findViewById(R.id.card_inner_title);
            ImageView extraIcon = (ImageView) view.findViewById(R.id.card_inner_extra_icon);
            TextView expires = (TextView) view.findViewById(R.id.card_inner_expires);

            main.setText(mAccount.getNumber());
            sub.setText(mAccount.getCategory());

            if (mAccount.getCategory().toLowerCase().equals("credit")) {
                int iconId = getExtraIcon(mAccount.getType().toLowerCase());
                if (iconId != -1) {
                    extraIcon.setImageResource(iconId);
                }
            }

            if (!mAccount.getExpires().isEmpty()) {
                expires.setText("exp. " + mAccount.getExpires());
            }
        }

        Button edit = (Button) view.findViewById(R.id.action_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.editButton(mAccount);
                }
            }
        });

        CardViewWrapper cardView = getCardView();
        CardThumbnailView thumb = cardView.getInternalThumbnailLayout();
        if (thumb != null) {
            ViewGroup.LayoutParams lp = thumb.getLayoutParams();
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) lp).setMargins(25, 0 ,0 , 5);
            }
        }

    }

    @Override
    public int getType() {
        if (mAccount.getCategory().equals("Cash")) {
            return 1;
        } else {
            return 0;
        }
    }

    // Helper method for creating expanding card
    public AccountCard createExpandCard(Account account, Activity activity) {

        // Create objects
        AccountCard card = new AccountCard(getContext(), account);
        ExpandAccountCard expand = new ExpandAccountCard(getContext(), account);

        expand.setmListener((ExpandAccountCard.ExpandListClick) activity);

        card.addCardExpand(expand);
        // Create and attach click to expand
        ViewToClickToExpand viewToClickToExpand = ViewToClickToExpand
                .builder().enableForExpandAction();
        card.setViewToClickToExpand(viewToClickToExpand);

        card.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
            @Override
            public void onExpandEnd(Card card) {
            }
        });

        card.setOnCollapseAnimatorEndListener(new Card.OnCollapseAnimatorEndListener() {
            @Override
            public void onCollapseEnd(Card card) {
            }
        });

        return card;

    } // createAccountCard

    private int getIcon(String category) {
        if (category != null) {
            category = category.toLowerCase();
            if (category.equals("cash")) {
                return R.drawable.ic_cash;
            } else if (category.equals("debit")) {
                return R.drawable.ic_debit;
            } else {
                return R.drawable.ic_credit;
            }
        }
        return -1;
    }

    private int getExtraIcon(String type) {
        if (type.equals("visa")) {
            return R.drawable.visa_noborder;
        } else if (type.equals("mastercard") || type.equals("master card")) {
            return R.drawable.mastercard_noborder;
        } else if (type.equals("american express") || type.equals("amex")) {
            return R.drawable.amex_noborder;
        } else {
            return -1;
        }
    }

    public void setmListener(EditAccountCallback mListener) {
        this.mListener = mListener;
    }

    // Custom header class for AccountCard
    public class AccountCardHeader extends CardHeader {

        private String mTitle;

        public AccountCardHeader(Context context, int innerLayout, String title) {
            super(context, innerLayout);
            this.mTitle = title;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            super.setupInnerViewElements(parent, view);

            TextView titleView = (TextView) view.findViewById(R.id.card_header_subtitle);
            if (titleView != null) {
                titleView.setText(mTitle);
            }
        }
    } // End AccountCardHeader

}

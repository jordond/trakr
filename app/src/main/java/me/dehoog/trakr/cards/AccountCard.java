package me.dehoog.trakr.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.base.CardViewWrapper;
import it.gmariotti.cardslib.library.view.component.CardThumbnailView;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.Account;

/**
 * Author:  jordon
 * Created: November, 15, 2014
 * 11:58 PM
 */
public class AccountCard extends Card {

    protected Account mAccount;

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
        icon.setDrawableResource(getIcon(mAccount.getCategory()));

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

        // Card content
        TextView main = (TextView) view.findViewById(R.id.card_inner_title);
        TextView sub = (TextView) view.findViewById(R.id.card_inner_subtitle);
        ImageView extraIcon = (ImageView) view.findViewById(R.id.card_inner_extra_icon);

        main.setText(mAccount.getNumber());
        sub.setText(mAccount.getCategory());

        if (mAccount.getCategory().toLowerCase().equals("credit")) {
            int iconId = getExtraIcon(mAccount.getType().toLowerCase());
            if (iconId != -1) {
                extraIcon.setImageResource(iconId);
            }
        }

        CardViewWrapper cardView = getCardView();
        CardThumbnailView thumb = cardView.getInternalThumbnailLayout();
        if (thumb != null) {
            ViewGroup.LayoutParams lp = thumb.getLayoutParams();
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) lp).setMargins(25, 0 ,0 , 5);
            }
        }

    }

    // Helper method for creating expanding card
    public AccountCard createExpandCard(Account account) {

        // Create objects
        AccountCard card = new AccountCard(getContext(), account);
        ExpandAccountCard expand = new ExpandAccountCard(getContext());

        card.addCardExpand(expand);

        // Create and attach click to expand
        ViewToClickToExpand viewToClickToExpand = ViewToClickToExpand
                .builder().enableForExpandAction();
        card.setViewToClickToExpand(viewToClickToExpand);

        // TODO add events to the expand events
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
        category = category.toLowerCase();
        if (category.equals("cash")) {
            return R.drawable.cash;
        } else if (category.equals("debit")) {
            return R.drawable.debit;
        } else {
            return R.drawable.creditcard;
        }
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

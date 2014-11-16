package me.dehoog.trakr.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.base.CardViewWrapper;
import it.gmariotti.cardslib.library.view.component.CardThumbnailView;
import me.dehoog.trakr.R;

/**
 * Author:  jordon
 * Created: November, 15, 2014
 * 11:58 PM
 */
public class AccountCard extends Card {

    protected String mTitle;
    protected String mContentMain;
    protected String mContentSub;
    protected int mIcon;

    public AccountCard(Context context, String title, String contentMain, String contentSub, int icon) {
        super(context, R.layout.card_account);
        this.mTitle = title;
        this.mContentMain = contentMain;
        this.mContentSub = contentSub;
        this.mIcon = icon;
        init();
    }

    public AccountCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public AccountCard(Context context) {
        super(context, R.layout.card_account);
    }

    private void init() {

        CardHeader header = new AccountCardHeader(getContext(), R.layout.card_account_header, mTitle);
        header.setTitle("Not Visible");
        header.setButtonExpandVisible(true);

        CardThumbnail icon = new CardThumbnail(getContext());
        icon.setDrawableResource(mIcon);

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

        main.setText(mContentMain);
        sub.setText(mContentSub);

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
    public AccountCard createExpandCard(String title, String contentMain, String contentSub, int icon) {

        // Create objects
        AccountCard card = new AccountCard(getContext(), title, contentMain, contentSub, icon);
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

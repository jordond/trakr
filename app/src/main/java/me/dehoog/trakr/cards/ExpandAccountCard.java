package me.dehoog.trakr.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import it.gmariotti.cardslib.library.internal.CardExpand;
import me.dehoog.trakr.R;

/**
 * Author:  jordon
 * Created: November, 16, 2014
 * 12:18 AM
 */
public class ExpandAccountCard extends CardExpand {

    public ExpandAccountCard(Context context) {
        super(context, R.layout.card_account_expand);
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

    }
}

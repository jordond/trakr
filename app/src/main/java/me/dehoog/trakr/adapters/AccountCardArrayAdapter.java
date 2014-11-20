package me.dehoog.trakr.adapters;

import android.content.Context;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

/**
 * Author:  jordon
 * Created: November, 19, 2014
 * 7:40 PM
 */
public class AccountCardArrayAdapter extends CardArrayAdapter {

    public AccountCardArrayAdapter(Context context, List<Card> cards) {
        super(context, cards);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}

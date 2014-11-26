package me.dehoog.trakr.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.Purchase;
import me.dehoog.trakr.models.User;

/**
 * Author:  jordon
 * Created: November, 26, 2014
 * 6:02 PM
 */
public class CategoryListCard extends CardWithList {

    private User mUser;
    private List<Purchase> mPurchases;
    private int mCategoryCount;

    public CategoryListCard(Context context, User user) {
        super(context);
        this.mUser = user;
        mPurchases = this.mUser.getAllPurchases();
    }

    @Override
    protected CardHeader initCardHeader() {
        CardHeader header = new CategoryListCardHeader(getContext(), R.layout.category_list_card_inner_header);
        header.setTitle("Categories");
        return null;
    }

    @Override
    protected void initCard() {

    }

    @Override
    protected List<ListObject> initChildren() {
        return null;
    }

    @Override
    public View setupChildView(int i, ListObject listObject, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public int getChildLayoutId() {
        return 0;
    }

    public class CategoryObject extends DefaultListObject {
        public String name;
        public String iconURL;
        public double total;

        public CategoryObject(Card parentCard) {
            super(parentCard);
        }
    }

    public class CategoryListCardHeader extends CardHeader {

        private String mTitle;
        private String mSubtitle;

        public CategoryListCardHeader(Context context, int innerLayout) {
            super(context, innerLayout);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            super.setupInnerViewElements(parent, view);

            TextView titleView = (TextView) view.findViewById(R.id.text_birth1);
            if (titleView != null) {
                titleView.setText(mTitle);
            }
            TextView subtitleView = (TextView) view.findViewById(R.id.text_birth2);
            if (subtitleView != null) {
                subtitleView.setText(mSubtitle);
            }
        }

        public void setmTitle(String mTitle) {
            this.mTitle = mTitle;
        }

        public void setmSubtitle(String mSubtitle) {
            this.mSubtitle = mSubtitle;
        }
    }

}

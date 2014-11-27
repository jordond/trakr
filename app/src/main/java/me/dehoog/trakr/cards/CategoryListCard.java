package me.dehoog.trakr.cards;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import me.dehoog.trakr.R;
import me.dehoog.trakr.fragments.SpendingFragment;
import me.dehoog.trakr.models.Purchase;
import me.dehoog.trakr.models.User;

/**
 * Author:  jordon
 * Created: November, 26, 2014
 * 6:02 PM
 */
public class CategoryListCard extends CardWithList {

    private User mUser;
    private List<SpendingFragment.CategoryInformation> mCategories;

    public CategoryListCard(Context context, User user, List<SpendingFragment.CategoryInformation> categories) {
        super(context);
        this.mUser = user;
        this.mCategories = categories;
    }

    @Override
    protected CardHeader initCardHeader() {
        CategoryListCardHeader header = new CategoryListCardHeader(getContext(), R.layout.category_list_card_inner_header);
        header.setmTitle("Categories");

        int count = mCategories.size();
        if (count > 1) {
            header.setmSubtitle(count + " categories");
        } else if (count == 1) {
            header.setmSubtitle(count + " category");
        } else {
            header.setmSubtitle("None");
        }
        return header;
    }

    @Override
    protected void initCard() {
    }

    @Override
    protected List<ListObject> initChildren() {

        DecimalFormat decimalFormat = new DecimalFormat("$###,###,###.00");
        List<ListObject> children = new ArrayList<ListObject>();

        for (SpendingFragment.CategoryInformation category : mCategories) {

            CategoryObject co = new CategoryObject(this);
            co.main = category.getCategory();
            co.sub = category.getSubCategory();
            co.iconURL = category.getIconUrl();
            co.total = String.valueOf(decimalFormat.format(category.getTotal()));
            children.add(co);
        }

        if (children.isEmpty()) {
            return null;
        } else {
            return children;
        }
    }

    @Override
    public View setupChildView(int i, ListObject listObject, View view, ViewGroup viewGroup) {
        ImageView icon = (ImageView) view.findViewById(R.id.category_list_card_inner_icon);
        TextView main = (TextView) view.findViewById(R.id.category_list_card_inner_title);
        TextView sub = (TextView) view.findViewById(R.id.category_list_card_inner_subtitle);
        TextView amount = (TextView) view.findViewById(R.id.category_list_card_inner_ammount);

        CategoryObject obj = (CategoryObject) listObject;
        main.setText(obj.main);
        sub.setText(obj.sub);
        amount.setText(obj.total);

        Ion.with(icon)
                .placeholder(R.drawable.ic_general_icon)
                .error(R.drawable.ic_general_icon)
                .load(obj.iconURL);

        return view;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.category_list_card_inner_main;
    }

    public class CategoryObject extends DefaultListObject {
        public String main;
        public String sub;
        public String iconURL;
        public String total;

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

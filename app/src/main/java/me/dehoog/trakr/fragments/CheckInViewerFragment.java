package me.dehoog.trakr.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.Merchant;
import me.dehoog.trakr.models.Purchase;

public class CheckInViewerFragment extends Fragment {

    private static final String ARG_CHECK_IN = "checkIn";

    private Purchase mCheckIn;
    private Merchant mMerchant;

    // UI Components
    @InjectView(R.id.merchant_name) TextView mMerchantName;
    @InjectView(R.id.merchant_address) TextView mMerchantAddress;
    @InjectView(R.id.merchant_type) TextView mMerchantType;
    @InjectView(R.id.merchant_phone) TextView mMerchantPhone;
    @InjectView(R.id.merchant_website) TextView mMerchantWebsite;
    @InjectView(R.id.check_in_date) TextView mCheckInDate;
    @InjectView(R.id.check_in_account) TextView mCheckInAccount;
    @InjectView(R.id.check_in_amount) TextView mCheckInAmount;

    // UI - Actions
    @OnClick(R.id.action_confirm)
    public void onClick() {
        close();
    }

    @OnClick(R.id.merchant_phone)
    public void openDialer() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mMerchant.getPhone()));
        startActivity(intent);
    }

    @OnClick(R.id.merchant_website)
    public void openUrl() {
        String url = mMerchant.getWebsite();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public static CheckInViewerFragment newInstance(Purchase checkIn) {
        CheckInViewerFragment fragment = new CheckInViewerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CHECK_IN, checkIn);
        fragment.setArguments(args);
        return fragment;
    }

    public CheckInViewerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCheckIn = (Purchase) getArguments().getSerializable(ARG_CHECK_IN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_in_viewer, container, false);

        ButterKnife.inject(this, view);
        setupUI();

        return view;
    }

    public void setupUI() {
        mMerchant = mCheckIn.getMerchant();
        mMerchantName.setText(mMerchant.getName());
        mMerchantAddress.setText(mMerchant.getLocation().getLongAddress());
        mMerchantType.setText(mCheckIn.getCategory().getDescription());
        mMerchantPhone.setText(mMerchant.getPhone());
        mMerchantWebsite.setText(mMerchant.getWebsite());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE MMM d, yyyy");
        mCheckInDate.setText(simpleDateFormat.format(mCheckIn.getDate()));
        mCheckInAccount.setText(mCheckIn.getAccount().getDescription());

        DecimalFormat decimalFormat = new DecimalFormat("$###,###,###.00");
        mCheckInAmount.setText(decimalFormat.format(mCheckIn.getAmount()));
    }

    private void close() {
        getFragmentManager().popBackStackImmediate();
    }

}

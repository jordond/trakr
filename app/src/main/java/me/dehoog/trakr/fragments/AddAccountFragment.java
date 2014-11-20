package me.dehoog.trakr.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.dehoog.trakr.R;
import me.dehoog.trakr.interfaces.AccountsInteraction;
import me.dehoog.trakr.interfaces.AddAccountInteraction;
import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.User;

public class AddAccountFragment extends Fragment {

    private static final String ARG_USER = "user";
    private static final String ARG_ACTION = "action";
    private static final String ARG_ACCOUNT = "account";

    private AddAccountInteraction mListener;

    private User mUser;
    private String mAction;
    private Account mAccount;

    private String mCategory;
    private String mType; //amex, visa, mastercard

    // UI - Text fields
    @InjectView(R.id.account_number) EditText mAccountNumber;
    @InjectView(R.id.expires) EditText mExpiry;
    @InjectView(R.id.descriptive_name) EditText mName;

    // UI - Action buttons
    @InjectView(R.id.action_confirm) Button mConfirm;
    @OnClick(R.id.action_confirm)
    public void onConfirm() {
        attemptAdd();
    }

    @OnClick(R.id.action_nfc)
    public void onReadNfcClick() {
    }

    @OnClick(R.id.action_cancel)
    public void onCancel() { close(); }

    // UI - Account type toggles
    @InjectView(R.id.toggle_group) RadioGroup mToggleGroup;
    @InjectView(R.id.toggle_cash) ToggleButton mCash;
    @InjectView(R.id.toggle_debit) ToggleButton mDebit;
    @InjectView(R.id.toggle_credit) ToggleButton mCredit;
    @OnClick({ R.id.toggle_cash, R.id.toggle_credit, R.id.toggle_debit })
    public void onToggle(ToggleButton button) {
        ((RadioGroup)button.getParent()).check(button.getId());

        switch (button.getId()) {
            case R.id.toggle_cash:
                mCategory = "Cash";
                mAccountNumber.setEnabled(false);
                mExpiry.setEnabled(false);
                break;
            case R.id.toggle_debit:
                mCategory = "Debit";
                mAccountNumber.setEnabled(true);
                mExpiry.setEnabled(true);
                break;
            case R.id.toggle_credit:
                mCategory = "Credit";
                mAccountNumber.setEnabled(true);
                mExpiry.setEnabled(true);
        }

        if (button.getId() == R.id.toggle_credit && button.isChecked()) {
            mType = "";
            PopupMenu pop = new PopupMenu(getActivity(), button);
            pop.getMenuInflater()
                    .inflate(R.menu.credit_type, pop.getMenu());
            pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    mType = String.valueOf(item.getTitle());
                    return true;
                }
            });
            pop.show();
            mType = mType.isEmpty() ? "Other" : mType;
        }
    }

    public static AddAccountFragment newInstance(User user, String action, Account account) {
        AddAccountFragment fragment = new AddAccountFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        args.putString(ARG_ACTION, action);
        args.putSerializable(ARG_ACCOUNT, account);
        fragment.setArguments(args);
        return fragment;
    }

    public AddAccountFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = (User) getArguments().getSerializable(ARG_USER);
            mAction = getArguments().getString(ARG_ACTION);
            mAccount = (Account) getArguments().getSerializable(ARG_ACCOUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_account, container, false);
        ButterKnife.inject(this, view);

        mToggleGroup.setOnCheckedChangeListener(ToggleListener);
        if (mAction.equals("add")) {
            mConfirm.setText("Add");
        } else {
            mConfirm.setText("Save");
        }

        if (mAccount != null) {
            mType = mAccount.getType();
            mAccountNumber.setText(mAccount.getNumber());
            mExpiry.setText(mAccount.getExpires());
            mName.setText(mAccount.getDescription());

            mCategory = mAccount.getCategory();
            if (mCategory.equals("Cash")) {
                mCash.toggle();
            } else if (mCategory.equals("Debit")) {
                mDebit.toggle();
            } else if (mCategory.equals("Credit")) {
                mCredit.toggle();
            }
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AddAccountInteraction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AddActionInteraction");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle("Add Account");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle("");
        }
    }

    public void close() {
        getFragmentManager().popBackStackImmediate();
    }

    static final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
            for (int j = 0; j < radioGroup.getChildCount() - 1; j++) {
                final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
                view.setChecked(view.getId() == i);
            }
        }
    };

    private void attemptAdd() {
        boolean cancel = false;
        View focusView = null;

        if (mAccount != null) {
            mAccount.setUser(mUser);
        } else {
            mAccount = new Account(mUser);
            mAccount.setCategory(mCategory);
        }

        if (mCategory == null) {
            focusView = mToggleGroup;
            cancel = true;
        } else if (mCategory.equals("Cash")) {
            String name = String.valueOf(mName.getText());
            if (name.isEmpty()) {
                cancel = true;
                focusView = mName;
            } else {
                mAccount.setDescription(name);
            }
        } else if (mCategory.equals("Debit") || mCategory.equals("Credit")) {
            String name = String.valueOf(mName.getText());
            String accountNum = String.valueOf(mAccountNumber.getText());
            String expiry = String.valueOf(mExpiry.getText());
            if (name.isEmpty()) {
                cancel = true;
                focusView = mName;
            } else {
                mAccount.setDescription(name);
            }
            if (expiry.isEmpty()) {
                cancel = true;
                focusView = mExpiry;
            } else {
                mAccount.setExpires(expiry);
            }
            if (accountNum.isEmpty()) {
                cancel = true;
                focusView = mAccountNumber;
            } else {
                mAccount.setNumber(accountNum);
            }
        }

        if (cancel) {
            focusView.requestFocus();
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(focusView);
        } else if (mAccount.isValid()) {
            mAccount.save();
            if (mListener != null) {
                mListener.onAddInteraction();
            }
            close();
        }
    }
}

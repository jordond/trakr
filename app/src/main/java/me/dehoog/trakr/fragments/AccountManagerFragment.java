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
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.doomonafireball.betterpickers.expirationpicker.ExpirationPickerBuilder;
import com.doomonafireball.betterpickers.expirationpicker.ExpirationPickerDialogFragment;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dehoog.trakr.R;
import me.dehoog.trakr.activities.CardReaderActivity;
import me.dehoog.trakr.interfaces.AddAccountInteraction;
import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.CardNFC;
import me.dehoog.trakr.models.User;

public class AccountManagerFragment extends Fragment {

    public static final int NFC_CARD_READ_REQUEST = 8;

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

    @InjectView(R.id.action_delete) ImageButton mDelete;
    @OnClick(R.id.action_delete)
    public void showDelete() {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("All transactions for this account will also be deleted.")
                .setConfirmText("Yes!")
                .setCancelText("I changed my mind")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog dialog) {
                        dialog.setTitleText("Cancelled!")
                                .setContentText("Phew that was close, your account is safe")
                                .setConfirmText("OK")
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog dialog) {
                        String message;
                        int count = mAccount.deleteAllTransactions();
                        if (count == 0) {
                            message = "Account '" + mAccount.getDescription() + "' was deleted!";
                        } else {
                            message = "Account '" + mAccount.getDescription() + "' was deleted along with " + count + " transactions!";
                        }
                        mAccount.delete();
                        mAccount = null;
                        dialog.setTitleText("Deleted!")
                                .setContentText(message)
                                .setConfirmText("OK")
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        if (mListener != null) {
                            mListener.onAddInteraction();
                        }
                        close();
                    }
                }).show();
    }

    @InjectView(R.id.action_nfc) ImageButton mReadCard;
    @OnClick(R.id.action_nfc)
    public void onReadNfcClick() {
        Intent intent = new Intent(getActivity(), CardReaderActivity.class);
        intent.putExtra("fromAccountManager", true);
        startActivityForResult(intent, NFC_CARD_READ_REQUEST);
    }

    @OnClick(R.id.action_cancel)
    public void onCancel() { close(); }

    @OnClick(R.id.expires)
    public void expiryDateClicked() {
        ExpirationPickerBuilder epb = new ExpirationPickerBuilder()
                .setFragmentManager(getFragmentManager())
                .setStyleResId(R.style.BetterPickersDialogFragment_Light);
        epb.addExpirationPickerDialogHandler(new ExpirationPickerDialogFragment.ExpirationPickerDialogHandler() {
            @Override
            public void onDialogExpirationSet(int i, int year, int day) {
                String date = "";
                if (day < 10) {
                    date = "0" + String.valueOf(day);
                } else {
                    date = String.valueOf(day);
                }
                date += "/" + String.valueOf(year).substring(2, 4);
                mExpiry.setText(date);
            }
        });
        epb.show();
    }

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
                mAccountNumber.setText("");
                mExpiry.setText("");
                break;
            case R.id.toggle_debit:
                mCategory = "Debit";
                mAccountNumber.setEnabled(true);
                break;
            case R.id.toggle_credit:
                mCategory = "Credit";
                mAccountNumber.setEnabled(true);
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

    public static AccountManagerFragment newInstance(User user, String action, Account account) {
        AccountManagerFragment fragment = new AccountManagerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        args.putString(ARG_ACTION, action);
        args.putSerializable(ARG_ACCOUNT, account);
        fragment.setArguments(args);
        return fragment;
    }

    public AccountManagerFragment() {}

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
        View view = inflater.inflate(R.layout.fragment_account_manager, container, false);
        ButterKnife.inject(this, view);

        mToggleGroup.setOnCheckedChangeListener(ToggleListener);
        if (mAction.equals("add")) {
            mConfirm.setText("Add");
            YoYo.with(Techniques.Tada)
                    .duration(1100)
                    .playOn(mReadCard);
        } else {
            mConfirm.setText("Save");
            mDelete.setVisibility(View.VISIBLE);
        }

        if (mAccount != null) {
            mType = mAccount.getType();
            mAccountNumber.setText(mAccount.getNumber());
            mAccountNumber.setEnabled(true);
            mExpiry.setText(mAccount.getExpires());
            mName.setText(mAccount.getDescription());

            mCategory = mAccount.getCategory();
            if (mCategory.equals("Cash")) {
                mCash.toggle();
                mAccountNumber.setText("");
                mAccountNumber.setEnabled(false);
                mExpiry.setText("");
            } else if (mCategory.equals("Debit")) {
                mDebit.toggle();
            } else if (mCategory.equals("Credit")) {
                mCredit.toggle();
            }
        }
        return view;
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
            mAccount.setCategory(mCategory);
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

            if (mCategory.equals("Credit")) {
                mAccount.setType(mType);
            } else {
                mAccount.setType("");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NFC_CARD_READ_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                processCardResult((CardNFC) data.getSerializableExtra("card"));
            }
        }
    }

    private void processCardResult(CardNFC card) {
        if (card != null) {
            mCredit.toggle();
            mCategory = "Credit";
            mType = card.getType();
            mAccountNumber.setText(card.getAccountNumber());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(card.getExpiry());
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            String exp = String.valueOf(month) + "/" + String.valueOf(year).substring(2, 4);
            mExpiry.setText(exp);
        }
    }

    public void setmListener(AddAccountInteraction mListener) {
        this.mListener = mListener;
    }
}

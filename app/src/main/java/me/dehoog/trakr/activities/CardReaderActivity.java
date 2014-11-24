package me.dehoog.trakr.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.devnied.emvnfccard.model.EmvCard;
import com.github.devnied.emvnfccard.parser.EmvParser;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.security.Provider;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import me.dehoog.trakr.R;
import me.dehoog.trakr.providers.NFCProvider;
import me.dehoog.trakr.tasks.NFCTask;
import me.dehoog.trakr.utils.NFCUtils;

public class CardReaderActivity extends Activity {

    private static final String TAG = CardReaderActivity.class.getSimpleName();

    private NFCUtils mNfcUtils;
    private ProgressDialog mDialog;
    private SweetAlertDialog mAlertDialog;

    private NFCProvider mProvider = new NFCProvider();
    private EmvCard mReadCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_reader);

        mNfcUtils = new NFCUtils(this);

        if (getIntent().getAction() == NfcAdapter.ACTION_TECH_DISCOVERED) {
            onNewIntent(getIntent());
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final Tag mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (mTag != null) {

            new NFCTask() {
                private IsoDep mTagComm;
                private EmvCard mCard;
                private boolean mException;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (mDialog == null) {
                        mDialog = ProgressDialog.show(CardReaderActivity.this, "Reading Card",
                                "Reading your card, please do not move the card", true, false);
                    } else {
                        mDialog.show();
                    }
                }

                @Override
                protected void doInBackground() {
                    mTagComm = IsoDep.get(mTag);
                    if (mTagComm == null) {
                        Crouton.makeText(CardReaderActivity.this, "Error reading card...", Style.ALERT).show();
                        return;
                    }
                    mException = false;
                    try {
                        mReadCard = null;
                        mTagComm.connect();
                        mProvider.setTagCom(mTagComm);

                        EmvParser parser = new EmvParser(mProvider, true);
                        mCard = parser.readEmvCard();
                    } catch (IOException e) {
                        mException = true;
                        Log.e(TAG, "doInBackground exception: " + e.getMessage());
                    } finally {
                        IOUtils.closeQuietly(mTagComm);
                    }
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (mDialog != null) {
                        mDialog.cancel();
                    }
                    if (!mException) {
                        if (mCard != null) {
                            if (StringUtils.isNotBlank(mCard.getCardNumber())) {
                                Crouton.makeText(CardReaderActivity.this, "Read card successfully!", Style.CONFIRM).show();
                                mReadCard = mCard;
                                cardReadSuccess();
                            }
                        } else {
                            Crouton.makeText(CardReaderActivity.this, "Card unknown error.. Only Credit supported", Style.INFO).show();
                        }
                    } else {
                        Crouton.makeText(CardReaderActivity.this, "Communication error, try again", Style.ALERT).show();
                    }
                }
            }.execute();
        }

    }

    private void cardReadSuccess() {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Success!")
                .setContentText("Card was successfully read, found account number: " + mReadCard.getCardNumber() + ", is this correct?")
                .setConfirmText("Yup!")
                .setCancelText("Try Again")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog dialog) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("card", mReadCard);
                        setResult(RESULT_OK,returnIntent);
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        mNfcUtils.enableDispatch();
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.cancel();
        }
        if (!NFCUtils.isNFCEnabled(this)) {
            mAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("NFC Error")
                    .setContentText("Either your device doesn't support NFC, or it is not enabled.")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog dialog) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            mAlertDialog.show();
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

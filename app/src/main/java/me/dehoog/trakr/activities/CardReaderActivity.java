package me.dehoog.trakr.activities;

import android.app.ActionBar;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.github.devnied.emvnfccard.model.EmvCard;
import com.github.devnied.emvnfccard.parser.EmvParser;
import com.skyfishjy.library.RippleBackground;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.security.Provider;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.CardNFC;
import me.dehoog.trakr.providers.NFCProvider;
import me.dehoog.trakr.tasks.NFCTask;
import me.dehoog.trakr.utils.NFCUtils;

public class CardReaderActivity extends Activity {

    private static final String TAG = CardReaderActivity.class.getSimpleName();

    private NFCUtils mNfcUtils;
    private SweetAlertDialog mAlertDialog;

    private NFCProvider mProvider = new NFCProvider();
    private EmvCard mReadCard;

    private RippleBackground mRippleBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_reader);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mRippleBackground = (RippleBackground) findViewById(R.id.ripple_background);
        mRippleBackground.startRippleAnimation();

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
                    Crouton.makeText(CardReaderActivity.this, "Reading card, do not move card!", Style.INFO).show();
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
                    if (!mException) {
                        if (mCard != null) {
                            if (StringUtils.isNotBlank(mCard.getCardNumber())) {
                                mReadCard = mCard;
                                cardReadSuccess();
                            } else {
                                Crouton.cancelAllCroutons();
                                Crouton.makeText(CardReaderActivity.this, "Unknown card type scanned, try another", Style.ALERT).show();
                            }
                        } else {
                            Crouton.makeText(CardReaderActivity.this, "Failed to read the card.. Only Credit cards are supported", Style.ALERT).show();
                        }
                    } else {
                        Crouton.makeText(CardReaderActivity.this, "Communication error, try again", Style.ALERT).show();
                    }
                }
            }.execute();
        }

    }

    private void cardReadSuccess() {
        final CardNFC card = new CardNFC(mReadCard.getCardNumber(), mReadCard.getExpireDate(), mReadCard.getType().getName());
        mRippleBackground.stopRippleAnimation();
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Successful Read!")
                .setContentText("Found a " + card.getType() + " with an account number of '" + card.getAccountNumber() + "'. Is this correct?")
                .setConfirmText("Yup!")
                .setCancelText("Try Again")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog dialog) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("card", card);
                        setResult(RESULT_OK, returnIntent);

                        mNfcUtils.disableDispatch();
                        mNfcUtils = null;
                        mAlertDialog = null;
                        mRippleBackground.stopRippleAnimation();

                        dialog.dismiss();
                        finish();
                    }
                }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog dialog) {
                        mRippleBackground.startRippleAnimation();
                        dialog.dismiss();
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
                            mAlertDialog = null;
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
        if (item.getItemId() == R.id.action_help) {
            new MaterialDialog.Builder(this)
                    .title("Reading your Card")
                    .content(getString(R.string.activity_card_reader_help_message))
                    .theme(Theme.LIGHT)
                    .positiveText("Sounds Good!")
                    .show();
        } else {
            mAlertDialog = null;
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

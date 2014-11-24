package me.dehoog.trakr.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.util.Log;

/**
 * Author:  jordon
 * Created: November, 24, 2014
 * 2:28 PM
 */
public class NFCUtils {

    private static final String TAG = NFCUtils.class.getSimpleName();

    private final NfcAdapter mNfcAdapter;
    private final PendingIntent mPendingIntent;
    private final Activity mActivity;

    private static final IntentFilter[] INTENT_FILTER = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED) };
    private static final String[][] TECH_LIST = new String[][] { { IsoDep.class.getName() } };

    public static boolean isNFCEnabled(final Context context) {
        boolean result = true;
        try {
            NfcAdapter adapter = NfcAdapter.getDefaultAdapter(context);
            result = adapter != null && adapter.isEnabled();
        } catch (UnsupportedOperationException e) {
            result = false;
            Log.e(TAG, "NFC Error: " + e.getMessage());
        }
        return result;
    }

    public NFCUtils(final Activity activity) {
        mActivity = activity;
        mNfcAdapter = NfcAdapter.getDefaultAdapter(mActivity);
        mPendingIntent = PendingIntent.getActivity(mActivity, 0,
                new Intent(mActivity, mActivity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    public void disableDispatch() {
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(mActivity);
        }
    }

    public void enableDispatch() {
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(mActivity, mPendingIntent, INTENT_FILTER, TECH_LIST);
        }
    }

    public NfcAdapter getNfcAdapter() {
        return mNfcAdapter;
    }

}

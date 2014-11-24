package me.dehoog.trakr.providers;

import android.nfc.tech.IsoDep;
import android.util.Log;

import com.github.devnied.emvnfccard.enums.SwEnum;
import com.github.devnied.emvnfccard.exception.CommunicationException;
import com.github.devnied.emvnfccard.parser.IProvider;
import com.github.devnied.emvnfccard.utils.TlvUtil;

import java.io.IOException;

import fr.devnied.bitlib.BytesUtils;

/**
 * Author:  jordon
 * Created: November, 24, 2014
 * 2:38 PM
 */
public class NFCProvider implements IProvider {

    private static final String TAG = NFCProvider.class.getSimpleName();

    private IsoDep mTagCom;

    @Override
    public byte[] transceive(byte[] pCommand) throws CommunicationException {
        byte[] response;
        try {
            response = mTagCom.transceive(pCommand);
        } catch (IOException e) {
            Log.e(TAG, "Error transceiver card: " + e.getMessage());
            throw new CommunicationException(e.getMessage());
        }
        Log.d(TAG, "response: " + BytesUtils.bytesToString(response));
        try {
            Log.d(TAG, "response: " + TlvUtil.prettyPrintAPDUResponse(response));
            SwEnum val = SwEnum.getSW(response);
            if (val != null) {
                Log.d(TAG, "response: " + val.getDetail());
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    public void setTagCom(final IsoDep tagCom) {
        this.mTagCom = tagCom;
    }

}

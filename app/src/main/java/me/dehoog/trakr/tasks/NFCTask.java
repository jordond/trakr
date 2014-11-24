package me.dehoog.trakr.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.github.devnied.emvnfccard.model.EmvCard;

/**
 * Author:  jordon
 * Created: November, 24, 2014
 * 2:54 PM
 */
public abstract class NFCTask extends AsyncTask<Void, Void, Object> {

    @Override
    protected Object doInBackground(final Void... params) {

        Object result = null;

        try {
            doInBackground();
        } catch (Exception e) {
            result = e;
            Log.e(NFCTask.class.getName(), e.getMessage(), e);
        }

        return result;
    }

    protected abstract void doInBackground();

}

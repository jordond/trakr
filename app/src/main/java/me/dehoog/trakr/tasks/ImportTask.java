package me.dehoog.trakr.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import me.dehoog.trakr.models.ImportResult;

/**
 * Author:  jordon
 * Created: November, 28, 2014
 * 4:56 PM
 */
public class ImportTask extends AsyncTask<Void, Void, ImportResult> {

    private ProgressDialog mDialog;
    private OnImportResult mListener;

    public ImportTask(Context context, OnImportResult listener) {
        this.mDialog = new ProgressDialog(context);
        this.mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        this.mDialog.setMessage("Retrieving data from bank");
        this.mDialog.show();
        super.onPreExecute();
    }

    @Override
    protected ImportResult doInBackground(Void... params) {
        // Introduce fake network lag, to represent external call


        return null;
    }

    @Override
    protected void onPostExecute(ImportResult result) {
        this.mDialog.dismiss();
        mListener.onResult(result);
    }

    @Override
    protected void onCancelled() {
        this.mDialog = null;
        mListener = null;
    }

    public interface OnImportResult {
        public void onResult(ImportResult importResult);
    }

}

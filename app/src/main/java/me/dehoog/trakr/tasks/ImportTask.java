package me.dehoog.trakr.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

import me.dehoog.trakr.models.ImportResult;

/**
 * Author:  jordon
 * Created: November, 28, 2014
 * 4:56 PM
 */
public class ImportTask extends AsyncTask<Void, Void, ImportResult> {

    private static final String TAG = ImportTask.class.getName();

    private ProgressDialog mDialog;
    private OnImportResult mListener;
    private Context mContext;

    public ImportTask(Context context, OnImportResult listener) {
        this.mDialog = new ProgressDialog(context);
        this.mListener = listener;
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        this.mDialog.setMessage("Synchronizing data...");
        this.mDialog.show();
        super.onPreExecute();
    }

    @Override
    protected ImportResult doInBackground(Void... params) {
        ImportResult importResult = new ImportResult();
        try {
            Thread.sleep(3500); // Simulate network lag

            InputStream inputStream = mContext.getAssets().open("bank_response.json");
            byte[] buffer = new byte[inputStream.available()];

            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");

            Gson gson = new Gson();
            importResult = gson.fromJson(json, ImportResult.class);
            importResult.setStatus(200);
            importResult.setStatus_message("OK");

            return importResult;

        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException: " + e.getMessage());
            importResult.setStatus(500);
            importResult.setStatus_message("InterruptedException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
            importResult.setStatus(500);
            importResult.setStatus_message("IOException: " + e.getMessage());
        }

        return importResult;
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

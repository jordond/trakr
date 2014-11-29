package me.dehoog.trakr.tasks;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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
        this.mDialog.setCanceledOnTouchOutside(false);
        this.mDialog.setCancelable(false);
        this.mDialog.show();
        super.onPreExecute();
    }

    @Override
    protected ImportResult doInBackground(Void... params) {
        ImportResult importResult = new ImportResult();
        try {
            if (!checkForNetwork()) {
                throw new NetworkErrorException();
            }

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
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException: " + e.getMessage());
            importResult.setStatus(500);
            importResult.setStatus_message("InterruptedException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
            importResult.setStatus(500);
            importResult.setStatus_message("IOException: " + e.getMessage());
        } catch (JsonParseException e) {
            Log.e(TAG, "JSON Parse: " + Arrays.toString(e.getStackTrace()));
            importResult.setStatus(400);
            importResult.setStatus_message("Server returned an invalid response.");
        } catch (NetworkErrorException e) {
            Log.e(TAG, "Network Error: " + Arrays.toString(e.getStackTrace()));
            importResult.setStatus(500);
            importResult.setStatus_message("Unable to connect to the internet.");
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

    public boolean checkForNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public interface OnImportResult {
        public void onResult(ImportResult importResult);
    }

}

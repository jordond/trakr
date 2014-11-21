package me.dehoog.trakr.tasks;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.dehoog.trakr.interfaces.PlacesSearchListener;

/**
 * Author:  jordon
 * Created: November, 20, 2014
 * 7:52 PM
 */
public class PlacesSearchTask extends AsyncTask<String, Void, String> {

    private PlacesSearchListener callback;

    public PlacesSearchTask(PlacesSearchListener cb) {
        this.callback = cb;
    }

    @Override
    protected String doInBackground(String... urls) {

        StringBuilder places = new StringBuilder();

        for (String url : urls) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet httpGet = new HttpGet(url);
                HttpResponse response = httpClient.execute(httpGet);

                StatusLine status = response.getStatusLine();
                if (status.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();

                    InputStream content = entity.getContent();
                    InputStreamReader input = new InputStreamReader(content);

                    BufferedReader reader = new BufferedReader(input);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        places.append(line);
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        return places.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        callback.onSearchComplete(result);
    }

    @Override
    protected void onCancelled() {
        callback.onSearchCancelled();
    }
}

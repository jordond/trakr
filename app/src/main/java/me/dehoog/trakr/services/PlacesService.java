package me.dehoog.trakr.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import me.dehoog.trakr.interfaces.PlacesSearchListener;
import me.dehoog.trakr.models.PlaceDetails;
import me.dehoog.trakr.models.Places;
import me.dehoog.trakr.models.PlacesResult;
import me.dehoog.trakr.tasks.PlacesSearchTask;

/**
 * Author:  jordon
 * Created: November, 20, 2014
 * 7:02 PM
 */
public class PlacesService extends Service implements PlacesSearchListener {

    private static PlacesService mInstance;

    private static final String PLACES_API = "AIzaSyATAz4Zi2av7206I5JFWqCBUzbzlpnLcdA";
    private static final String PLACES_BASE = "https://maps.googleapis.com/maps/api/place/";
    private static final String PLACES_NEARBY = "nearbysearch/json?";
    private static final String PLACES_DETAILS = "details/json?";

    private Context mContext;

    private int mRadius = 100;

    private Location mLocation;
    private double mLatitude;
    private double mLongitude;

    public PlacesService() { }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static PlacesService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PlacesService();
            mInstance.setmContext(context);
        }
        return  mInstance;
    }

    // Searchers
    public void nearbySearch(Location location) {

        if (location == null) {
            setToCurrentLocation();
            location = mLocation;
            if (location == null) {
                return;
            }
        }

        LatLng latLng = locationToLatLng(location);

        String url = PLACES_BASE + PLACES_NEARBY
                + "key=" + PLACES_API
                + "&location=" + latLng.latitude + "," + latLng.longitude
                + "&radius=" + mRadius;

        // Call async get
        PlacesSearchTask searchTask = new PlacesSearchTask(this);
        searchTask.execute(url);
    }

    //TODO implement
    public List<Places> textSearch() {
        return null;
    }

    //TODO implement
    public PlaceDetails placeDetailSearch() {
        return null;
    }

    // Callbacks
    @Override
    public void onSearchComplete(String result) {
        List<Places> places = parseJSON(result);
        if (places != null) {

        } else {

        }
    }

    @Override
    public void onSearchCancelled() {
        //TODO Do something... maybe
    }

    // Helpers
    public boolean setToCurrentLocation() {
        GPSTracker tracker = GPSTracker.getInstance();
        mLocation = tracker.getLocation(mContext);
        if (mLocation == null) {
            return false;
        } else {
            return true;
        }
    }

    public LatLng locationToLatLng(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        return new LatLng(lat, lng);
    }

    public List<Places> parseJSON(String json) {
        PlacesResult result = new PlacesResult();
        try {
            Gson gson = new Gson();
            result = gson.fromJson(json, PlacesResult.class);
        } catch (Exception e) {
            Log.d("parseJSON", e.getMessage());
        }
        return result.getResults();
    }

    public int getmRadius() {
        return mRadius;
    }

    public void setmRadius(int mRadius) {
        this.mRadius = mRadius;
    }

    public Location getmLocation() {
        return mLocation;
    }

    public void setmLocation(Location mLocation) {
        this.mLocation = mLocation;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

}

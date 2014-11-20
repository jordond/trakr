package me.dehoog.trakr.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Author:  jordon
 * Created: November, 20, 2014
 * 3:21 PM
 * Some code borrowed from http://stackoverflow.com/questions/18446619/get-gps-coordinates-with-asynctask
 */
public class GPSTracker extends Service implements LocationListener {

    private static final String TAG = GPSTracker.class.getSimpleName();
    private Context mContext;
    private static GPSTracker mInstance;

    private static final long MIN_DISTANCE_CHANGE = 0;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 30 * 1;

    protected LocationManager mLocationManager;

    private boolean mIsGPSEnabled = false;
    private boolean mIsNetworkEnabled = false;
    private boolean mCanGetLocation = false;
    private Location mLocation;
    private double mLatitude;
    private double mLongitude;

    public GPSTracker() {
        mContext = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static GPSTracker getInstance() {
        if (mInstance == null) {
            mInstance = new GPSTracker();
        }
        return  mInstance;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public Location getLocation(Context context) {
        mContext = context;
        try {
            mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            mIsGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            mIsNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (mIsGPSEnabled || mIsNetworkEnabled) {
                this.mCanGetLocation = true;
                if (mIsNetworkEnabled) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE, this);;
                    if (mLocationManager != null) {
                        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (mLocation != null) {
                            mLatitude = mLocation.getLatitude();
                            mLongitude = mLocation.getLongitude();
                        }
                    }
                } // Network enabled

                if (mIsGPSEnabled) {
                    if (mLocation == null) {
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE, this);
                        if (mLocationManager != null) {
                            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (mLocation != null) {
                                mLatitude = mLocation.getLatitude();
                                mLongitude = mLocation.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d("GPSTracker", "ERROR: " + e.getMessage());
        }
        return mLocation;
    }

    public void stopUsingGPS() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(GPSTracker.this);
        }
    }

    public double getLatitude() {
        if (mLocation != null) {
            mLatitude = mLocation.getLatitude();
        }
        return mLatitude;
    }

    public double getLongitude() {
        if (mLocation != null) {
            mLongitude = mLocation.getLongitude();
        }
        return mLongitude;
    }

    public boolean canGetLocation() {
        return this.mCanGetLocation;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

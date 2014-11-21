package me.dehoog.trakr.activities;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.PlaceDetails;
import me.dehoog.trakr.models.Places;
import me.dehoog.trakr.models.PlacesResult;
import me.dehoog.trakr.services.GPSTracker;
import me.dehoog.trakr.services.PlacesService;

public class CheckInActivity extends Activity implements PlacesService.PlacesInterface{

    private static final int MAP_ZOOM = 14;

    private GoogleMap mMap;

    private GPSTracker mTracker;
    private PlacesService mPlacesService;

    private Location mCurrentLocation;

    private List<Places> mPlaces;
    private List<Marker> mMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            mTracker = GPSTracker.getInstance();
            mPlacesService = PlacesService.getInstance(this);
            mPlacesService.setmListener(this);

            setUpMapIfNeeded();
        } else {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Google Play Services not available!")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(false);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mCurrentLocation = mTracker.getLocation(getApplication());
                setLocation(mCurrentLocation);
                return true;
            }
        });

        mCurrentLocation = mTracker.getLocation(this);
        setLocation(mCurrentLocation);
    }

    private void setLocation(Location location) {
        if (location != null) {
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.zoomTo(MAP_ZOOM));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng), 3000, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_check_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_search) {
            testGson();
        }

        return super.onOptionsItemSelected(item);
    }

    private void testGson() {
        mPlacesService.nearbySearch(null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTracker != null) {
            mTracker.stopUsingGPS();
        }
    }

    @Override
    public void onPlacesReturned(List<Places> places) {
        mPlaces = places;

    }

    @Override
    public void onPlaceDetailsReturned(PlaceDetails details) {

    }
}

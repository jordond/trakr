package me.dehoog.trakr.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.Place;
import me.dehoog.trakr.models.PlaceDetails;
import me.dehoog.trakr.services.GPSTracker;
import me.dehoog.trakr.services.PlacesService;

public class CheckInActivity extends Activity implements PlacesService.PlacesInterface{

    private static final int MAP_ZOOM = 15;

    private GoogleMap mMap;

    private GPSTracker mTracker;
    private PlacesService mPlacesService;

    private Location mCurrentLocation;

    private List<Place> mPlaces = new ArrayList<Place>();
    private List<Marker> mMarkers = new ArrayList<Marker>();

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
    public void onPlacesReturned(List<Place> places) {
        for (Marker m : mMarkers) {
            if (m != null) {
                m.remove();
            }
        }
        mMarkers.clear();

        for (Place place : places) {
            addMarker(place);
            mPlaces.add(place);
        }

    }

    private void addMarker(Place place) {
        final Marker marker = mMap.addMarker(new MarkerOptions()
                .position(place.getLatLng())
                .title(place.getName()));
        mMarkers.add(marker);

        Ion.with(getApplicationContext())
                .load(place.getIcon())
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        int index = mMarkers.indexOf(marker);
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(result));
                        mMarkers.set(index, marker);
                    }
                });

    }

    @Override
    public void onPlaceDetailsReturned(PlaceDetails details) {

    }
}

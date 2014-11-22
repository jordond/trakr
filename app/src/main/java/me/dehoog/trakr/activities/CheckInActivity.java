package me.dehoog.trakr.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.Place;
import me.dehoog.trakr.models.PlaceDetails;
import me.dehoog.trakr.services.GPSTracker;
import me.dehoog.trakr.services.PlacesService;

public class CheckInActivity extends Activity implements PlacesService.PlacesInterface{

    private static final int MAP_ZOOM = 16;

    private GoogleMap mMap;

    private GPSTracker mTracker;
    private PlacesService mPlacesService;

    private Location mCurrentLocation;

    private List<Place> mPlaces = new ArrayList<Place>();
    private List<Marker> mMarkers = new ArrayList<Marker>();

    // UI Components
    @InjectView(R.id.panel_layout) SlidingUpPanelLayout mMerchantLayout;
    @InjectView(R.id.merchant_icon) ImageView mMerchantIcon;
    @InjectView(R.id.merchant_name) TextView mMerchantName;
    @InjectView(R.id.merchant_address) TextView mMerchantAddress;

    @InjectView(R.id.panel_slide_symbol) ImageView mSlideSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        if (getActionBar() != null) {
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            mTracker = GPSTracker.getInstance();
            mPlacesService = PlacesService.getInstance(this);
            mPlacesService.setmListener(this);

            ButterKnife.inject(this);

            mMerchantLayout.hidePanel();

            setUpMapIfNeeded();
            mPlacesService.nearbySearch(null);
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
                mMerchantLayout.hidePanel();
                mCurrentLocation = mTracker.getLocation(getApplication());
                setLocation(convertLocation(mCurrentLocation));
                mPlacesService.nearbySearch(null);
                return true;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMerchantLayout.hidePanel();
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMerchantLayout.hidePanel();
                mPlacesService.nearbySearch(convertLatLng(latLng));
                setLocation(latLng);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (Place place : mPlaces) {
                    if (place.getId().equals(marker.getSnippet())) {
                        if (mMerchantLayout.isPanelHidden()) {
                            mMerchantLayout.showPanel();
                        }
                        Ion.with(mMerchantIcon)
                                .placeholder(R.drawable.ic_general_icon)
                                .error(R.drawable.ic_general_icon)
                                .load(place.getIcon());

                        mMerchantName.setText(place.getName());
                        mMerchantAddress.setText(place.getVicinity());
                        break;

                        //TODO fetch extra place details
                    }
                }
                return true;
            }
        });

        mCurrentLocation = mTracker.getLocation(this);
        setLocation(convertLocation(mCurrentLocation));
    }

    private LatLng convertLocation(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    private Location convertLatLng(LatLng location) {
        Location loc = new Location(LocationManager.GPS_PROVIDER);
        loc.setLatitude(location.latitude);
        loc.setLongitude(location.longitude);
        return loc;
    }

    private void setLocation(LatLng location) {
        if (location != null) {
            mMap.moveCamera(CameraUpdateFactory.zoomTo(MAP_ZOOM));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(location), 3000, null);
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
        }

        return super.onOptionsItemSelected(item);
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
        for (Place place : places) {
            if (!place.shouldExclude()) {
                addMarker(place);
                mPlaces.add(place);
            }
        }
    }

    public void clearMarkers() {
        for (Marker marker : mMarkers) {
            if (marker != null) {
                marker.remove();
            }
        }
        mMarkers.clear();
    }

    private void addMarker(Place place) {
        final Marker marker = mMap.addMarker(new MarkerOptions()
                .position(place.getLatLng())
                .title(place.getName())
                .snippet(place.getId()));

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

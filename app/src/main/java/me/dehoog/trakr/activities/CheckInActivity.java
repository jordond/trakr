package me.dehoog.trakr.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.Address;
import me.dehoog.trakr.models.Category;
import me.dehoog.trakr.models.Merchant;
import me.dehoog.trakr.models.Place;
import me.dehoog.trakr.models.PlaceDetails;
import me.dehoog.trakr.models.Purchase;
import me.dehoog.trakr.models.User;
import me.dehoog.trakr.services.GPSTracker;
import me.dehoog.trakr.services.PlacesService;

// TODO add a "view" feature to this activity, so the user can view a previous transaction, only show the one marker on the map
public class CheckInActivity extends Activity implements PlacesService.PlacesInterface {

    private static final int MAP_ZOOM = 16;                             // Holds the zoom level for the map

    private GoogleMap mMap;                                             // Google Maps object

    private GPSTracker mTracker;                                        // Service for getting the current location, from GPS or network
    private PlacesService mPlacesService;                               // Service for interacting with the Google Places API

    private Location mCurrentLocation;                                  // Holds the current location

    private List<Place> mPlaces = new ArrayList<Place>();                   // Holds a list of all the returned Place objects
    private List<Marker> mMarkers = new ArrayList<Marker>();                 // A list of Google Map Markers

    private User mUser = new User();                                    // The current logged in user object
    private Merchant mMerchant = new Merchant();                        // Holds the information for the selected map marker
    private Marker mSelectedMarker;                                     // Current selected map marker

    private List<Category> mCategories = new ArrayList<Category>();     // Contains a list of found categories
    private List<String> mIconUrls = new ArrayList<String>();           // Contains the urls of of all found categories
    private List<String> mFilterUrls = new ArrayList<String>();         // Contains the urls of the filtered categories
    private Integer[] mSelectedFilters;

    private SimpleDateFormat mDateFormat;                               // Date pattern "EEEE MMM d, yyyy"
    private Date mDate; // I hate dates so much                         // Object to store the current date, I used it as a fix for a bug

    // UI Components
    @InjectView(R.id.panel_layout) SlidingUpPanelLayout mMerchantLayout;
    @InjectView(R.id.panel_header) LinearLayout mPanelHeader;

    // Panel Header
    @InjectView(R.id.merchant_icon) ImageView mMerchantIcon;
    @InjectView(R.id.merchant_name) TextView mMerchantTitle;
    @InjectView(R.id.merchant_address) TextView mMerchantSubtitle;
    @InjectView(R.id.panel_slide_symbol) ImageView mSlideSymbol;

    // Panel Content
    @InjectView(R.id.panel_merchant_address) TextView mPanelAddress;
    @InjectView(R.id.panel_merchant_type) TextView mPanelType; // category
    @InjectView(R.id.panel_merchant_phone) TextView mPanelPhone;

    @OnClick(R.id.panel_merchant_phone)
    public void openDialer() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mMerchant.getPhone()));
        startActivity(intent);
    }
    @InjectView(R.id.panel_merchant_website) TextView mPanelWebsite;
    @OnClick(R.id.panel_merchant_website)
    public void openUrl() {
        String url = mMerchant.getWebsite();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @InjectView(R.id.panel_transaction_date) TextView mPanelDate;   //TODO add in a date picker
    @InjectView(R.id.panel_transaction_account) Spinner mPanelAccount;
    @InjectView(R.id.panel_transaction_amount) EditText mPanelAmount;

    // Panel Content - Buttons
    @OnClick(R.id.action_save)
    public void addTransaction() {
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(mPanelAmount.getText())) {
            focusView = mPanelAmount;
            cancel = true;
        }
        if (mPanelAccount.getSelectedItem() == null) {
            focusView = mPanelAccount;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(focusView);
        } else {
            saveTransaction();
        }
    }

    @OnClick(R.id.action_cancel)
    public void closePanel() {
        mPanelAccount.setSelection(0);
        mPanelAmount.setText("");
        mMerchantLayout.collapsePanel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        String email = settings.getString("email", "none");
        mUser = new User().findUser(email);

        if (mUser == null || mUser.getAllAccounts().isEmpty()) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("You don't have any accounts setup!")
                    .setConfirmText("Ok, I'll add some")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            finish();
                        }
                    }).show();
        } else {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if (ConnectionResult.SUCCESS == resultCode) {
                mTracker = GPSTracker.getInstance();
                ButterKnife.inject(this);

                mPlacesService = PlacesService.getInstance(getApplicationContext());
                mPlacesService.setmListener(this);
                fadeView(mMerchantSubtitle, true); //fix jittery bug

                mMerchantLayout.hidePanel();
                mMerchantLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                    @Override
                    public void onPanelSlide(View view, float v) {

                    }

                    @Override
                    public void onPanelCollapsed(View view) {
                        fadeView(mMerchantSubtitle, false);
                        mSlideSymbol.setImageResource(R.drawable.ic_plus_skinny);
                    }

                    @Override
                    public void onPanelExpanded(View view) {
                        fadeView(mMerchantSubtitle, true);
                        mSlideSymbol.setImageResource(R.drawable.ic_chevron_down);
                    }

                    @Override
                    public void onPanelAnchored(View view) {
                        fadeView(mMerchantSubtitle, true);
                        mSlideSymbol.setImageResource(R.drawable.ic_chevron_down);
                    }

                    @Override
                    public void onPanelHidden(View view) {
                        fadeView(mMerchantSubtitle, true);
                    }
                });

                mDateFormat = new SimpleDateFormat("EEEE MMM d, yyyy");
                setUpMapIfNeeded();
                mPlacesService.nearbySearch(mCurrentLocation);


                setupAccountSpinner();
            } else {
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Google Play Services not available!")
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent intent = new Intent();
                                setResult(RESULT_CANCELED, intent);
                                finish();
                            }
                        }).show();
            }
        }
    }

    private void setupAccountSpinner() {
        List<Account> accounts = mUser.getAllAccounts();
        if (!accounts.isEmpty()) {
            List<String> names = new ArrayList<String>();
            for (Account a : accounts) {
                names.add(a.getDescription());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mPanelAccount.setAdapter(adapter);
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
        mMap.getUiSettings().setZoomControlsEnabled(false);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mMerchantLayout.hidePanel();
                mCurrentLocation = mTracker.getLocation(getApplication());
                clearForNewSearch();
                setLocation(convertLocation(mCurrentLocation), true);
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
                clearForNewSearch();
                mCurrentLocation = convertLatLng(latLng);
                mPlacesService.nearbySearch(mCurrentLocation);
                setLocation(latLng, false);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!mMerchantLayout.isPanelHidden() && mSelectedMarker != null && mSelectedMarker.getId().equals(marker.getId())) {
                    return true;
                }
                for (Place place : mPlaces) {
                    mMerchant.setPlace(place);
                    if (place.getId().equals(marker.getSnippet())) {
                        if (mMerchantLayout.isPanelHidden()) {
                            mMerchantLayout.showPanel();
                        }
                        mSelectedMarker = marker;
                        mPlacesService.placeDetailSearch(place); // Get details about the selected place
                        Ion.with(mMerchantIcon)
                                .placeholder(R.drawable.ic_general_icon)
                                .error(R.drawable.ic_general_icon)
                                .load(place.getIcon());

                        mMerchantTitle.setText(place.getName());
                        String address = place.getVicinity() == null ? place.getFormatted_address() : place.getVicinity();
                        mMerchantSubtitle.setText(address);
                        break;
                    }
                }
                return true;
            }
        });

        mCurrentLocation = mTracker.getLocation(this);
        setLocation(convertLocation(mCurrentLocation), true);
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

    private void setLocation(LatLng location, boolean zoom) {
        if (location != null) {
            if (zoom) {
                mMap.moveCamera(CameraUpdateFactory.zoomTo(MAP_ZOOM));
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLng(location), 1800, null);
        }
    }

    private void clearForNewSearch() {
        mCategories.clear();
        mIconUrls.clear();
        mPlaces.clear();
        clearMarkers();
        mSelectedFilters = null;
    }

    private void fadeView(View view, boolean out) {
        if (out) {
            YoYo.with(Techniques.FadeOut)
                    .duration(300)
                    .playOn(view);
        } else {
            YoYo.with(Techniques.FadeIn)
                    .duration(300)
                    .playOn(view);
        }
    }

    public void saveTransaction() {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Want to Check In?")
                .setContentText("Are you sure you want to add this Check In")
                .setConfirmText("Check In!")
                .setCancelText("Nope")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog dialog) {
                        Account account = new Account().findByDescription(mPanelAccount.getSelectedItem().toString());

                        if (account != null) {
                            Purchase transaction = new Purchase(account,
                                    Double.valueOf(mPanelAmount.getText().toString()));
                            transaction.setDate(mDate);

                            mMerchant.getCategory().save();
                            transaction.setCategory(mMerchant.getCategory());

                            mMerchant.getLocation().save();
                            mMerchant.save();
                            transaction.setMerchant(mMerchant);
                            transaction.save();

                            double total = account.getTotal() + Double.valueOf(mPanelAmount.getText().toString());
                            account.setTotal(total);
                            account.save();

                            Intent intent = new Intent();
                            intent.putExtra("add", true);
                            setResult(RESULT_OK, intent);
                            dialog.dismiss();
                            finish();
                        } else {
                            dialog.setTitleText("Error!")
                                    .setContentText("Something went wrong...")
                                    .setConfirmText("OK")
                                    .showCancelButton(false)
                                    .setCancelClickListener(null)
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        if (mMerchantLayout.isPanelExpanded()) {
            mMerchantLayout.collapsePanel();
        } else {
            super.onBackPressed();
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
            showSearchDialog();
        } else if (id == R.id.homeAsUp) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        } else if (id == R.id.action_filter) {
            showFilterDialog();
        }
        mMerchantLayout.collapsePanel();
        mMerchantLayout.hidePanel();

        return super.onOptionsItemSelected(item);
    }

    EditText searchInput;
    private void showSearchDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Search nearby")
                .customView(R.layout.check_in_text_search)
                .positiveText("search")
                .negativeText("cancel")
                .callback(new MaterialDialog.Callback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        clearForNewSearch();
                        String query = searchInput.getText().toString();
                        mPlacesService.textSearch(query, 600, mCurrentLocation);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                }).build();
        searchInput = (EditText) dialog.getCustomView().findViewById(R.id.search_query);
        dialog.show();
    }

    private void showFilterDialog() {
        final String[] types = new String[mCategories.size()];
        final Integer[] ints = new Integer[mCategories.size()];
        for (int i = 0; i < types.length; i++) {
            types[i] = mCategories.get(i).getName();
            ints[i] = i;
        }
        if (mSelectedFilters == null) {
            mSelectedFilters = ints;
        }

        new MaterialDialog.Builder(this)
                .title("Filter by Category")
                .items(types)
                .itemsCallbackMultiChoice(mSelectedFilters, new MaterialDialog.ListCallbackMulti() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        mFilterUrls.clear();
                        for (Integer i : integers) {
                            mFilterUrls.add(mIconUrls.get(i));
                        }
                        mSelectedFilters = integers;
                        filterMarkers();
                    }
                })
                .positiveText("Filter")
                .show();
    }

    public void filterMarkers() {
        clearMarkers();
        for (int i = 0; i < mPlaces.size(); i++) {
            Place place = mPlaces.get(i);
            if (mFilterUrls.contains(place.getIcon())) {
                addMarker(place);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTracker != null) {
            mTracker.stopUsingGPS();
        }
        if (mPlacesService != null) {
            mPlacesService.setmListener(null);
        }
    }

    // Places Service handler + map helpers
    @Override
    public void onPlacesReturned(List<Place> places) {
        for (Place place : places) {
            if (!place.shouldExclude()) {

                Category type = new Category();
                type.setIcon(place.getIcon());
                type.setName(type.parseTypeFromURL(place.getIcon()));

                if (mIconUrls.contains(type.getIcon())) {
                    for (Category c : mCategories) {
                        if (c.getIcon().equals(type.getIcon())) {
                            mCategories.set(mCategories.indexOf(c), type);
                            break;
                        }
                    }
                } else {
                    mCategories.add(type);
                    mIconUrls.add(type.getIcon());
                }
                mPlaces.add(place);
                addMarker(place);
            }
        }
        mSelectedFilters = null;
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
        if (details != null) {
            Place place = mMerchant.getPlace();

            mMerchant = new Merchant().findOrCreate(details.getPlace_id(), details.getName());
            mMerchant.setPhone(details.getFormatted_phone_number());
            mMerchant.setPlaceId(details.getPlace_id());
            mMerchant.setWebsite(details.getUrl());

            Category category = new Category().findOrCreate(details.typeToString(0));
            category.setIcon(details.getIcon());
            category.setDescription(details.typesToString());
            mMerchant.setCategory(category);

            Address address = new Address().findOrCreate(details.getFormatted_address());
            address.setLatitude(details.getLatitude());
            address.setLongitude(details.getLongitude());
            address.setAddress(details.getStreetAddress());
            address.setPostal(details.getPostalCode());
            address.setCity(details.getCity());
            address.setProvince(details.getProvince(true)); // long form
            address.setCountry(details.getCountry(true)); // long form
            mMerchant.setLocation(address);

            mMerchant.setPlace(place); // Might be useful to keep around

            mPanelAddress.setText(mMerchant.getLocation().getLongAddress()); // Might change to short address
            mPanelType.setText(details.typesToString());
            mPanelPhone.setText(mMerchant.getPhone());
            mPanelWebsite.setText(mMerchant.getWebsite());

            mPanelDate.setText(mDateFormat.format(new Date()));
            mDate = new Date();
        }
    }
}
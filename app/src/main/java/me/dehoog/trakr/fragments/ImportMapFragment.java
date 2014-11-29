package me.dehoog.trakr.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.dehoog.trakr.R;
import me.dehoog.trakr.models.Address;
import me.dehoog.trakr.models.Category;
import me.dehoog.trakr.models.Merchant;
import me.dehoog.trakr.models.Place;
import me.dehoog.trakr.models.PlaceDetails;
import me.dehoog.trakr.models.Purchase;
import me.dehoog.trakr.models.User;
import me.dehoog.trakr.services.PlacesService;

// I created a new fragment rather than using the CheckInActivity.class because I don't need
// all of its features, and it's a tad messy.
public class ImportMapFragment extends Fragment {

    private static final String TAG = ImportMapFragment.class.getSimpleName();
    private static final String ARG_USER_ID = "userID";
    private static final String ARG_CHECK_IN = "checkIn";

    private static final int MAP_ZOOM = 13;

    private GoogleMap mMap;
    private FragmentManager fragmentManager;
    private PlacesService mPlacesService;

    private List<Place> mPlaces = new ArrayList<Place>();
    private List<Marker> mMarkers = new ArrayList<Marker>();

    private long mUserID;
    private User mUser;
    private Merchant mMerchant = new Merchant();
    private Purchase mCheckIn;
    private OnCheckedIn mListener;
    private Marker mSelectedMarker;

    private DatePickerDialog.OnDateSetListener mDateListener;
    private DatePickerDialog mDialog;
    private SimpleDateFormat mDateFormat;
    private Date mDate;

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

    @InjectView(R.id.panel_transaction_date) TextView mPanelDate;
    @OnClick(R.id.panel_transaction_date)
    public void selectDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mDialog = new DatePickerDialog(getActivity(), mDateListener, year, month, day);
        mDialog.show();
    }
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
        if (mPanelAccount.getSelectedItemPosition() == 0) {
            focusView = mPanelAccount;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(focusView);
        } else {
            //saveTransaction();
        }
    }

    @OnClick(R.id.action_cancel)
    public void closePanel() {
        mPanelAccount.setSelection(0);
        mPanelAmount.setText("");
        mMerchantLayout.collapsePanel();
    }

    public static ImportMapFragment newInstance(long id, Purchase checkIn) {
        ImportMapFragment fragment = new ImportMapFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_USER_ID, id);
        args.putSerializable(ARG_CHECK_IN, checkIn);
        fragment.setArguments(args);
        return fragment;
    }

    public ImportMapFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserID = getArguments().getLong(ARG_USER_ID);
            mCheckIn = (Purchase) getArguments().getSerializable(ARG_CHECK_IN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_check_in, container, false);
        ButterKnife.inject(this, view);

        mUser = User.findById(User.class, mUserID);

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS != resultCode) {
            mListener.onError("Google play services is not available.");
        } else {
            mPlacesService = PlacesService.getInstance(getActivity());
            fadeView(mMerchantSubtitle, true);

            mMerchantLayout.hidePanel();
            mDateFormat = new SimpleDateFormat("EEEE MMM d, yyyy");

            setupListeners();

            String[] accountName = { mCheckIn.getAccount().getDescription() };
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, accountName);
            mPanelAccount.setAdapter(adapter);
            mPanelAmount.setEnabled(false);

            DecimalFormat df = new DecimalFormat("$###,###,###.00");
            mPanelAmount.setText(df.format(mCheckIn.getAmount()));
            mPanelAmount.setEnabled(false);

            setUpMapIfNeeded();
        }
        return view;
    }

    private void setUpMapIfNeeded() {
        if (mMap == null && fragmentManager != null) {
            mMap = ((MapFragment) fragmentManager.findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMerchantLayout.hidePanel();
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
                        mPlacesService.placeDetailSearch(place);
                        Ion.with(mMerchantIcon)
                                .placeholder(R.drawable.ic_general_icon)
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
        mPlacesService.textSearch(mCheckIn.getMerchant().getName(), 3000, null);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(MAP_ZOOM));
    }

    // Helpers
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

    //TODO save transaction

    // Listeners
    public void setupListeners() {
        mPlacesService.setmListener(new PlacesService.PlacesInterface() {
            @Override
            public void onPlacesReturned(List<Place> places) {
                LatLng location = places.get(0).getLatLng();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(location), 1800, null);
                for (Place place : places) {
                    if (!place.shouldExclude()) {
                        mPlaces.add(place);
                        addMarker(place);
                    }
                }
            }

            @Override
            public void onPlaceDetailsReturned(PlaceDetails details) {
                processDetails(details);
            }
        });

        mMerchantLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {}

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
            public void onPanelAnchored(View view) {}

            @Override
            public void onPanelHidden(View view) {}
        });

        mDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, monthOfYear, dayOfMonth);

                mPanelDate.setText(mDateFormat.format(c.getTime())); // for display
                mDate = c.getTime();    // for saving
                mDialog.dismiss();
            }
        };

    }

    public void processDetails(PlaceDetails details) {
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

        Ion.with(getActivity())
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
    public void onDestroy() {
        super.onDestroy();
        mPlacesService.setmListener(null);
    }

    public void setOnCheckIn(OnCheckedIn onCheckIn) {
        this.mListener = onCheckIn;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public interface OnCheckedIn {
        public void onConfirmCheckIn();
        public void onCancelCheckIn();
        public void onError(String message);
    }

}

package me.dehoog.trakr.models;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:  jordon
 * Created: November, 20, 2014
 * 6:38 PM
 */
public class Place {

    private static final String[] TYPE_EXCLUSIONS = {
            "bus_station", "cemetery", "church", "park", "place_of_worship", "police", "roofing_contractor",
            "rv_park", "school", "synagogue", "fire_station", "funeral_home", "neighborhood",
            "hindu_temple", "hospital", "local_government_office", "mosque"};

    private String id;
    private String name;
    private String icon;
    private String place_id;
    private String vicinity;
    private String formatted_address; // for text search
    private List<String> types;
    private PlacesLocation geometry;
    private LatLng latLng;

    // Helper
    public double getLatitude() {
        double lat = -1.0;
        if (this.geometry != null) {
            lat = this.geometry.getLocation().getLat();
        }
        return lat;
    }

    public double getLongitude() {
        double lng = -1.0;
        if (this.geometry != null) {
            lng = this.geometry.getLocation().getLng();
        }
        return lng;
    }

    public LatLng getLatLng() {
        if (this.latLng == null) {
            if (this.geometry != null) {
                return new LatLng(this.getLatitude(), this.getLongitude());
            }
        } else {
            return this.latLng;
        }
        return null;
    }

    public boolean shouldExclude() { // Check if the place should be excluded
        ArrayList<String> types = new ArrayList<String>(this.types);
        for (String s : TYPE_EXCLUSIONS) {
            if (types.contains(s)){
                Log.d("PlaceShouldExclude", "Excluding " + this.name);
                return true;
            }
        }
        return false;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public PlacesLocation getGeometry() {
        return geometry;
    }

    public void setGeometry(PlacesLocation geometry) {
        this.geometry = geometry;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}



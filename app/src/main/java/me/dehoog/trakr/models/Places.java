package me.dehoog.trakr.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Author:  jordon
 * Created: November, 20, 2014
 * 6:38 PM
 */
public class Places {

    private String id;
    private String name;
    private String place_id;
    private String vicinity;
    private List<String> types;
    private PlacesLocation geometry;

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
        if (this.geometry != null) {
            return new LatLng(this.getLatitude(), this.getLongitude());
        }
        return null;
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
}



package me.dehoog.trakr.models;

import android.location.Location;

import com.orm.SugarRecord;

/**
 * Created by jordon on 2014-11-09.
 */
public class Address extends SugarRecord<Address> {

    // Properties
    private Location coords;
    private String address;
    private String province;
    private String country;
    private String postal;

    // Constructors
    public Address() {
    }

    public Address(Location coords) {
        this.coords = coords;
    }

    public Address(Location coords, String address) {
        this.coords = coords;
        this.address = address;
    }

    public Address(Location coords, String address, String province, String country, String postal) {
        this.coords = coords;
        this.address = address;
        this.province = province;
        this.country = country;
        this.postal = postal;
    }

    // Accessors
    public Location getCoords() {
        return coords;
    }

    public void setCoords(Location coords) {
        this.coords = coords;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }
}

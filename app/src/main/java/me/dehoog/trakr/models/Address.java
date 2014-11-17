package me.dehoog.trakr.models;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by jordon on 2014-11-09.
 */
public class Address extends SugarRecord<Address> implements Serializable {

    // Properties
    private double latitude;
    private double longitude;
    private String address;
    private String province;
    private String country;
    private String postal;

    // Constructors
    public Address() {
    }

    public Address(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public Address(double lat, double lon, String address) {
        this.latitude = lat;
        this.longitude = lon;
        this.address = address;
    }

    public Address(double lat, double lon, String address, String province, String country, String postal) {
        this.latitude = lat;
        this.longitude = lon;
        this.address = address;
        this.province = province;
        this.country = country;
        this.postal = postal;
    }

    // Accessors


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

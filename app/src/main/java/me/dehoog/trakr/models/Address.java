package me.dehoog.trakr.models;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.List;

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
    private String city;
    private String longAddress;

    // Constructors
    public Address() {
    }

    public Address(String longAddress) {
        this.longAddress = longAddress;
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

    // Helpers
    public Address findOrCreate(String address) {
        Address found = null;
        List<Address> result = Address.find(Address.class, "long_address = ?", address);
        if (!result.isEmpty()) {
            found = result.get(0);
        } else {
            found = new Address(address);
        }
        return found;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLongAddress() {
        return longAddress;
    }

    public void setLongAddress(String longAddress) {
        this.longAddress = longAddress;
    }
}

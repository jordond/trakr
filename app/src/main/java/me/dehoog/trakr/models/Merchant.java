package me.dehoog.trakr.models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.Serializable;

/**
 * Created by jordon on 2014-11-09.
 */
public class Merchant extends SugarRecord<Merchant> implements Serializable {

    // Properties
    private String name;
    private Address location;
    private String description;
    private String phone;
    private String website;
    private String placeId;
    private Category category;

    @Ignore
    private Place place;

    // Constructors
    public Merchant() {
    }

    public Merchant(String name) {
        this.name = name;
    }

    public Merchant(String name, Address location) {
        this.name = name;
        this.location = location;
    }

    public Merchant(String name, Address location, String description) {
        this.name = name;
        this.location = location;
        this.description = description;
    }

    // Accessors
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getLocation() {
        return location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}

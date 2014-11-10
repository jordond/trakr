package me.dehoog.trakr.models;

import com.orm.SugarRecord;

/**
 * Created by jordon on 2014-11-09.
 */
public class Merchant extends SugarRecord<Merchant> {

    // Properties
    private String name;
    private Address location;
    private String description;

    // Constructors
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
}

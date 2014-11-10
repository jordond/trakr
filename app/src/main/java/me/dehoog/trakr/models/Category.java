package me.dehoog.trakr.models;

import com.orm.SugarRecord;

/**
 * Created by jordon on 2014-11-09.
 */
public class Category extends SugarRecord<Category> {

    // Properties
    private String name;
    private String description;
    private byte[] icon;

    // Constructors
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Category(String name, String description, byte[] icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    // Accessors
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }
}

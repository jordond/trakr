package me.dehoog.trakr.models;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jordon on 2014-11-09.
 */
public class Category extends SugarRecord<Category> implements Serializable {

    // Properties
    private String name;
    private String description;
    private String icon;
    private String iconName;

    // Constructors
    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Category(String name, String description, String icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    // Helper methods
    public Category findOrCreate(String name) {
        Category found = null;
        List<Category> request = Category.find(Category.class, "name = ?", name);
        if (!request.isEmpty()) {
            found = request.get(0);
        } else {
            found = new Category(name);
            found.save();
        }
        return found;
    }

    public String getIconName() {
        if (this.iconName == null) {
            return parseTypeFromURL(this.icon);
        }
        return this.iconName;
    }

    public String parseTypeFromURL(String url) {
        if (this.iconName == null) {
            if (url == null) {
                url = this.icon;
            }
            String raw = url.substring(url.lastIndexOf('/') + 1, url.length());
            raw = raw.substring(0, raw.lastIndexOf('-'));
            raw = raw.replaceAll("_", " ").toLowerCase();
            if (raw.equals("geocode")) {
                raw = "general";
            }
            return this.iconName = raw.substring(0, 1).toUpperCase() + raw.substring(1);
        }
        return this.iconName;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

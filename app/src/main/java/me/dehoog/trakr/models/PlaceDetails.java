package me.dehoog.trakr.models;

import java.util.List;

/**
 * Author:  jordon
 * Created: November, 20, 2014
 * 7:12 PM
 */
public class PlaceDetails {

    private List<AddressComponents> address_components;
    private String formatted_address;
    private String formatted_phone_number;
    private String icon;
    private String id;
    private String name;
    private String place_id;
    private List<String> types;
    private String url;
    private String vicinity;
    private String website;
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

    public int getIndexOfComponent(String component) {
        for (AddressComponents ac : this.address_components) {
            if (ac.getTypes().contains(component)) {
                return this.address_components.indexOf(ac);
            }
        }
        return -1;
    }

    public String getStreetNumber() {
        int index = getIndexOfComponent("street_number");
        if (index != -1) {
            AddressComponents ac = this.address_components.get(index);
            return ac.getLong_name();
        }
        return "";
    }

    public String getRoute() {
        int index = getIndexOfComponent("route");
        if (index != -1) {
            AddressComponents ac = this.address_components.get(index);
            return ac.getLong_name();
        }
        return "";
    }

    public String getStreetAddress() {
        String num = getStreetNumber();
        String street = getRoute();
        if (num.isEmpty()) {
            return street;
        } else {
            return num + ' ' + street;
        }
    }

    public String getPostalCode() {
        int index = getIndexOfComponent("postal_code");
        if (index != -1) {
            AddressComponents ac = this.address_components.get(index);
            return ac.getLong_name();
        }
        return "";
    }

    public String getCity() {
        int index = getIndexOfComponent("locality");
        if (index != -1) {
            AddressComponents ac = this.address_components.get(index);
            return ac.getLong_name();
        }
        return "";
    }

    public String getProvince(boolean longName) {
        int index = getIndexOfComponent("administrative_area_level_1");
        if (index != -1) {
            AddressComponents ac = this.address_components.get(index);
            if (longName) {
                return ac.getLong_name();
            } else {
                return ac.getShort_name();
            }
        }
        return "";
    }

    public String getCountry(boolean longName) {
        int index = getIndexOfComponent("country");
        if (index != -1) {
            AddressComponents ac = this.address_components.get(index);
            if (longName) {
                return ac.getLong_name();
            } else {
                return ac.getShort_name();
            }
        }
        return "";
    }

    public String typeToString() {
        String type = "";
        if (this.types != null) {
            String raw = this.types.get(0);
            raw = raw.replaceAll("_", " ").toLowerCase();
            type = raw.substring(0, 1).toUpperCase() + raw.substring(1);
        }
        return type;
    }

    public List<AddressComponents> getAddress_components() {
        return address_components;
    }

    public void setAddress_components(List<AddressComponents> address_components) {
        this.address_components = address_components;
    }

    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public void setFormatted_phone_number(String formatted_phone_number) {
        this.formatted_phone_number = formatted_phone_number;
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

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFormatted_address() {

        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public PlacesLocation getGeometry() {
        return geometry;
    }

    public void setGeometry(PlacesLocation geometry) {
        this.geometry = geometry;
    }

    public class AddressComponents {
        private String long_name;
        private String short_name;
        private List<String> types;

        public String getLong_name() {
            return long_name;
        }

        public void setLong_name(String long_name) {
            this.long_name = long_name;
        }

        public String getShort_name() {
            return short_name;
        }

        public void setShort_name(String short_name) {
            this.short_name = short_name;
        }

        public List<String> getTypes() {
            return types;
        }

        public void setTypes(List<String> types) {
            this.types = types;
        }
    }
}

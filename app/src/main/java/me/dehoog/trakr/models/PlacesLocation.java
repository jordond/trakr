package me.dehoog.trakr.models;

/**
 * Author:  jordon
 * Created: November, 20, 2014
 * 6:49 PM
 */
public class PlacesLocation {

    private Coordinates location;

    public Coordinates getLocation() {
        return location;
    }

    public void setLocation(Coordinates location) {
        this.location = location;
    }

    public class Coordinates {
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }
}

package me.dehoog.trakr.models;

import java.util.List;

/**
 * Author:  jordon
 * Created: November, 20, 2014
 * 7:14 PM
 */
public class PlaceDetailsResult {

    private PlaceDetails result;
    private String status;

    public PlaceDetails getResults() {
        return result;
    }

    public void setResult(PlaceDetails result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

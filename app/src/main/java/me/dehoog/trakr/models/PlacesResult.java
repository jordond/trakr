package me.dehoog.trakr.models;

import java.util.List;

/**
 * Author:  jordon
 * Created: November, 20, 2014
 * 6:35 PM
 */
public class PlacesResult {

    private String next_page_token;
    private List<Place> results;
    private String status;

    public boolean isMoreResults() {
        return next_page_token != null;
    }

    public String getNext_page_token() {
        return next_page_token;
    }

    public void setNext_page_token(String next_page_token) {
        this.next_page_token = next_page_token;
    }

    public List<Place> getResults() {
        return results;
    }

    public void setResults(List<Place> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

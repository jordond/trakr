package me.dehoog.trakr.models;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jordon on 2014-11-09.
 */
public class Account extends SugarRecord<Account> {

    // Properties
    private int number;
    private String name;
    private String description;
    private int branch;
    private List<Purchase> purchases;
    private double total;

    // Constructors
    public Account() {
    }

    public Account(int number, String name, int branch) {
        this.number = number;
        this.name = name;
        this.branch = branch;
        this.purchases = new ArrayList<Purchase>();
    }

    // Accessor
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

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

    public int getBranch() {
        return branch;
    }

    public void setBranch(int branch) {
        this.branch = branch;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}

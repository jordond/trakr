package me.dehoog.trakr.models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;

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
    private double total;

    @Ignore
    private List<Purchase> purchases;

    // Constructors
    public Account() {
    }

    public Account(int number, String name, int branch) {
        this.number = number;
        this.name = name;
        this.branch = branch;
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

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<Purchase> getPurchases() {
        return Select.from(Purchase.class)
                .where(Condition.prop("account").eq(this.getId().toString()))
                .list();
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }
}

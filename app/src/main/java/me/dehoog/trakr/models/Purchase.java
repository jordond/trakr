package me.dehoog.trakr.models;

import com.orm.SugarRecord;

import java.sql.Date;
import java.util.List;


/**
 * Created by jordon on 2014-11-09.
 */
public class Purchase extends SugarRecord<Purchase> {

    // Properties
    private User user;
    private double amount;
    private Merchant merchant;
    private Category category;
    private Date date;
    private String notes;

    // Constructors
    public Purchase() {
    }

    public Purchase(double amount, Category category, Date date, Merchant merchant) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.merchant = merchant;
    }

    // Accessors
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

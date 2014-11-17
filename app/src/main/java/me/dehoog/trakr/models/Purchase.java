package me.dehoog.trakr.models;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jordon on 2014-11-09.
 */
public class Purchase extends SugarRecord<Purchase> implements Serializable {

    // Properties
    private Account account;
    private double amount;
    private Merchant merchant;
    private Category category;
    private Date date;
    private String notes;

    // Constructors
    public Purchase() {
    }

    public Purchase(Account account, double amount) {
        this.account = account;
        this.amount = amount;
    }

    public Purchase(double amount, Category category, Date date, Merchant merchant) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.merchant = merchant;
    }

    // Helper methods


    // Accessors
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

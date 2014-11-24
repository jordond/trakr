package me.dehoog.trakr.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:  jordon
 * Created: November, 24, 2014
 * 5:58 PM
 */
public class CardNFC implements Serializable {

    private String accountNumber;
    private Date expiry;
    private String type;

    public CardNFC() {
    }

    public CardNFC(String accountNumber, Date expiry, String type) {
        this.accountNumber = accountNumber;
        this.expiry = expiry;
        this.type = type;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

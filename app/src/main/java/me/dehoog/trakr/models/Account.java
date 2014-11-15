package me.dehoog.trakr.models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * Created by jordon on 2014-11-09.
 */
public class Account extends SugarRecord<Account> {

    // Properties
    private String number;
    private User user;
    private String category; // cash, credit, debit
    private String type; // mastercard, visa, american express
    private String description;
    private int branch;
    private double total;
    private String expires;

    @Ignore
    private List<Purchase> purchases;

    // Constructors
    public Account() {
    }

    public Account(String number, String category, String type, String expires) { //card reader
        this.number = number;
        this.category = category;
        this.type = type;
        this.expires = expires;
    }

    public Account(User user, String number, String category) {
        this.user = user;
        this.number = number;
        this.category = category;
    }

    public Account(String number, String name, int branch) {
        this.number = number;
        this.type = name;
        this.branch = branch;
    }

    // Helper methods
    public Account findAccount(String accountNumber) {
        Account found = null;
        List<Account> request = Account.find(Account.class, "number = ?", accountNumber);
        if (!request.isEmpty()) {
            found = request.get(0);
        }
        return found;
    }

    // Accessor
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

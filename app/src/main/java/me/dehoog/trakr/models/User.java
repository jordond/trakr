package me.dehoog.trakr.models;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jordon on 2014-11-09.
 */
public class User extends SugarRecord<User> {

    // Properties
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private Address address;
    private List<Account> accounts;
    private List<Address> locations;

    // Constructors
    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.accounts = new ArrayList<Account>();
        this.locations = new ArrayList<Address>();
    }

    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accounts = new ArrayList<Account>();
        this.locations = new ArrayList<Address>();
    }

    // Accessors
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Address> getLocations() {
        return locations;
    }

    public void setLocations(List<Address> locations) {
        this.locations = locations;
    }
}

package me.dehoog.trakr.models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jordon on 2014-11-09.
 */
public class User extends SugarRecord<User> {

    // Properties
    private String email;
    private String password;
    private String salt;
    private String firstName;
    private String lastName;
    private String phone;
    private Address address;
    private boolean firstLogin;

    @Ignore
    private List<Account> accounts;

    public List<Account> getAccounts() {
        return Select.from(Account.class)
                .where(Condition.prop("user").eq(this.getId().toString()))
                .list();
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    // Constructors
    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.salt = generateSalt();
        this.password = generateHash(password);
    }

    public User(String email, String password, String salt) {
        this.email = email;
        this.salt = salt;
        this.password = generateHash(password);
    }

    // Helper methods
    public String generateSalt() {
        SecureRandom sr;
        byte[] salt = new byte[16];
        try {
            sr = SecureRandom.getInstance("SHA1PRNG");
            sr.nextBytes(salt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return salt.toString();
    }

    public String generateHash(String password) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(getSalt().getBytes());
            byte[] bytes = md.digest(password.getBytes());
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
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

    public String getSalt() {
        if (salt.isEmpty()) {
            this.salt = generateSalt();
        }
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }
}

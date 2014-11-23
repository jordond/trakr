package me.dehoog.trakr.models;

import android.util.Patterns;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Author:  jordon
 * Created: November, 11, 2014
 *          3:21 PM
 */
public class User extends SugarRecord<User> implements Serializable {

    // Properties
    private String username;
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

    @Ignore List<Purchase> purchases;

    public List<Account> getAccounts() {
        if (this.accounts == null) {
            return this.getAllAccounts();
        } else {
            return accounts;
        }
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

    public User findUser(String email) {
        User found = null;
        List<User> request = User.find(User.class, "email = ?", email);
        if (!request.isEmpty()) {
            found = request.get(0);
        }
        return found;
    }

    public boolean usernameExists(String username) {
        List<User> request = User.find(User.class, "username = ?", username);
        return !request.isEmpty();
    }

    public boolean emailExists(String email) {
        List<User> request = User.find(User.class, "email = ?", email);
        return !request.isEmpty();
    }

    public List<Account> getAllAccounts() {
        if (accounts == null) {
            return accounts = Account.find(Account.class, "user = ?", String.valueOf(this.getId()));
        } else {
            return accounts;
        }
    }

    public List<Purchase> getAllPurchases() {
        purchases = new ArrayList<Purchase>();
        for (Account account : this.getAllAccounts() ) {
            for (Purchase purchase : account.getAllPurchases()) {
                purchases.add(purchase);
            }
        }
        return purchases;
    }

    // Validators
    public static boolean isUsernameValid(String username) {
        return username.length() > 4;
    }

    public static boolean isEmailValid(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    // Accessors

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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
        this.password = generateHash(password);
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

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }
}
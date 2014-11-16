package me.dehoog.trakr.helpers;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

import me.dehoog.trakr.models.Account;
import me.dehoog.trakr.models.Address;
import me.dehoog.trakr.models.Category;
import me.dehoog.trakr.models.Merchant;
import me.dehoog.trakr.models.Purchase;
import me.dehoog.trakr.models.User;

/**
 * Author:  jordon
 * Created: November, 14, 2014
 * 4:09 PM
 */
public class SeedDatabase {

    public SeedDatabase() {

    }

    public void all() {
        if (users()) {
            if (accounts()) {
                if (purchases()) {
                    categories();
                }
            }
        }
    }

    public boolean users() {
        for (int i = 0; i < 2; i++) {
            User u = new User("test" + String.valueOf(i) + "@test.com", "password");
            u.setUsername("username" + String.valueOf(i));
            u.save();
        }
        List<User> users = User.listAll(User.class);
        return users.size() == 2;
    }

    public boolean accounts() {
        User u = new User().findUser("test0@test.com");
        if (u == null) {
            return false;
        }
        Account a = new Account(u, "91237912397132", "Cash");
        a.setDescription("Seeded account #1");
        a.setBranch(32);
        a.setExpires("10/10");
        a.save();

        a = new Account(u, "2343222232322", "Debit");
        a.setType("Visa");
        a.setDescription("Seeded account #2");
        a.setBranch(12);
        a.setExpires("1/12");
        a.save();

        a = new Account(u, "111111111112", "Credit");
        a.setType("Visa");
        a.setDescription("Seeded account #3");
        a.setBranch(11);
        a.setExpires("4/12");
        a.save();

        a = new Account(u, "9999999999999", "Credit");
        a.setType("Master Card");
        a.setDescription("Seeded account #4");
        a.setBranch(33);
        a.setExpires("24/10");
        a.save();

        List<Account> accs = Account.listAll(Account.class);
        return accs.size() == 4;
    }

    public boolean purchases() {
        Account a = new Account().findAccount("91237912397132");
        if (a == null) {
            return false;
        }

        double lat = -11.77455;
        double lon = 66.89461;
        Address address = new Address(lat, lon);
        address.setCountry("Canada");
        address.setProvince("ON");
        address.setPostal("N4E1R2");
        address.save();

        Merchant m = new Merchant("Tim Hortons", address);
        m.save();

        Purchase p = new Purchase(a, 14.99);
        p.setMerchant(m);

        Category c = new Category().findOrCreate("fast_food");
        c.save();

        p.setCategory(c);

        p.setDate(new Date());

        p.save();

        return true;
    }

    public void categories() {
        Category c = new Category("gym");
        c.save();
        c = new Category("fast_food");
        c.save();
        c = new Category("bank");
        c.save();
        c = new Category("theatre");
        c.save();
        c = new Category("mall");
        c.save();
        c = new Category("garbage_dump");
        c.save();
    }

}

package me.dehoog.trakr.helpers;

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
        users();
        accounts();
        merchents();
        purchases();
        categories();
        addresses();
    }

    public void users() {
        for (int i = 0; i < 6; i++) {
            User u = new User("test" + String.valueOf(i) + "@test.com", "password");
            u.setUsername("username" + String.valueOf(i));
            u.save();
        }
    }

    public void accounts() {

    }

    public void merchents() {

    }

    public void purchases() {

    }

    public void categories() {

    }

    public void addresses() {

    }

}

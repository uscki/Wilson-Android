package me.blackwolf12333.appcki;

import java.io.Serializable;

/**
 * Created by peter on 11/27/15.
 */
public class User implements Serializable {

    public String TOKEN;
    public boolean loggedIn;

    String firstName;
    String lastName;

    public User(String token) {
        this.TOKEN = token;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

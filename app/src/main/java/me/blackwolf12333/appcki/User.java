package me.blackwolf12333.appcki;

import me.blackwolf12333.appcki.generated.organisation.Person;

/**
 * Created by peter on 11/27/15.
 */
public class User {

    public String TOKEN;
    public boolean loggedIn;

    Person person;

    public User(String token, Person person) {
        this.TOKEN = token;
        this.person = person;
        this.loggedIn = false;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}

package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.api.PersonAPI;

/**
 * Created by peter on 12/30/15.
 */
public class NewPersonEvent {
    PersonAPI.Person person;

    public NewPersonEvent(PersonAPI.Person person) {
        this.person = person;
    }
}

package me.blackwolf12333.appcki.events;

import me.blackwolf12333.appcki.generated.organisation.Person;

/**
 * Created by peter on 12/30/15.
 */
public class PersonEvent {
    Person person;

    public PersonEvent(Person person) {
        this.person = person;
    }
}

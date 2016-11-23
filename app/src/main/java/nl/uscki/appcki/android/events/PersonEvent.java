package nl.uscki.appcki.android.events;

import nl.uscki.appcki.android.generated.organisation.Person;

/**
 * Created by peter on 12/30/15.
 */
public class PersonEvent {
    Person person;

    public PersonEvent(Person person) {
        this.person = person;
    }
}

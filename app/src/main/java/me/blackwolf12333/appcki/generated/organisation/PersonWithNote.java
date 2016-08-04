package me.blackwolf12333.appcki.generated.organisation;

import java.util.ArrayList;
import java.util.List;

import me.blackwolf12333.appcki.generated.meeting.EnrolledPerson;
import me.blackwolf12333.appcki.generated.meeting.Preference;
import me.blackwolf12333.appcki.generated.meeting.Slot;

/**
 * Created by peter on 7/30/16.
 */
public class PersonWithNote {
    String person;
    String note;

    public PersonWithNote(String person, String note) {
        this.person = person;
        this.note = note;
    }

    public String getPerson() {
        return person;
    }

    public String getNote() {
        return note;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PersonWithNote) {
            PersonWithNote p = (PersonWithNote) o;
            return p.getPerson().equals(this.person);
        }
        return false;
    }

    public static List<PersonWithNote> fromEnrolledPersons(List<EnrolledPerson> participants) {
        List<PersonWithNote> persons = new ArrayList<>();
        for (EnrolledPerson person : participants) {
            persons.add(new PersonWithNote(person.getName(), ""));
        }
        return persons;
    }

    public static List<PersonWithNote> fromSlotPreferencesAvailable(Slot slot) {
        List<PersonWithNote> persons = new ArrayList<>();
        for (Preference p : slot.getPreferences()) {
            if (p.getCanattend()) {
                persons.add(new PersonWithNote(p.getPerson().getName(), p.getNotes()));
            }
        }
        return  persons;
    }

    public static List<PersonWithNote> fromSlotPreferencesUnavailable(Slot slot) {
        List<PersonWithNote> persons = new ArrayList<>();
        for (Preference p : slot.getPreferences()) {
            if (!p.getCanattend()) {
                persons.add(new PersonWithNote(p.getPerson().getName(), p.getNotes()));
            }
        }
        return  persons;
    }
}

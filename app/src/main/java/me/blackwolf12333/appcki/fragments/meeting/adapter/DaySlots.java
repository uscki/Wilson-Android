package me.blackwolf12333.appcki.fragments.meeting.adapter;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.blackwolf12333.appcki.generated.meeting.Slot;

/**
 * Created by peter on 7/16/16.
 */
public class DaySlots {
    List<Slot> slots;
    String day;

    public DaySlots() {

    }

    public DaySlots(String day, List<Slot> slots) {
        this.day = day;
        this.slots = slots;
    }

    public static List<DaySlots> fromSlots(List<Slot> slots) {
        HashMap<String, List<Slot>> sorted = new HashMap<>();
        for (Slot slot : slots) {
            DateTime dateTime = new DateTime(slot.getStarttime());
            String key = dateTime.toString("EEEE dd MMMM YYYY");
            if (sorted.containsKey(key)) {
                sorted.get(key).add(slot);
            } else {
                List<Slot> dayslots = new ArrayList<>();
                dayslots.add(slot);
                sorted.put(key, dayslots);
            }
        }

        List<DaySlots> result = new ArrayList<>();
        for (String key : sorted.keySet()) {
            result.add(new DaySlots(key, sorted.get(key)));
        }
        return result;
    }

    public String getDay() {
        return day;
    }

    public List<Slot> getSlots() {
        return slots;
    }
}

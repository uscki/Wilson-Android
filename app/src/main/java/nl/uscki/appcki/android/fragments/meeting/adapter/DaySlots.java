package nl.uscki.appcki.android.fragments.meeting.adapter;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import nl.uscki.appcki.android.generated.meeting.Slot;

/**
 * Created by peter on 7/16/16.
 */
public class DaySlots {
    List<Slot> slots;
    String day;
    boolean isCollapsed;

    public DaySlots() {

    }

    public DaySlots(String day, List<Slot> slots) {
        this.day = day;
        this.slots = slots;
        this.isCollapsed = false;
    }

    public static List<DaySlots> fromSlots(List<Slot> slots) {
        TreeMap<Long, List<Slot>> sorted = new TreeMap<>();
        for (Slot slot : slots) {
            DateTime slotDateTime = new DateTime(slot.getStarttime())
                    .withTime(0,0,0,0);

            if (sorted.containsKey(slotDateTime.getMillis())) {
                sorted.get(slotDateTime.getMillis()).add(slot);
            } else {
                List<Slot> dayslots = new ArrayList<>();
                dayslots.add(slot);
                sorted.put(slotDateTime.getMillis(), dayslots);
            }
        }

        List<DaySlots> result = new ArrayList<>();
        for (Long key : sorted.keySet()) {
            DateTime keyDateTime = new DateTime(key);
            result.add(new DaySlots(keyDateTime.toString("EEEE dd MMMM YYYY"), sorted.get(key)));
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

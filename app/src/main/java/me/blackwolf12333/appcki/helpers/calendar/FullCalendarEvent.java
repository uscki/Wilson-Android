package me.blackwolf12333.appcki.helpers.calendar;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michielvanliempt on 10/01/15.
 */
public class FullCalendarEvent extends SimpleCalendarEvent {

    static final List<String> RRULES = new ArrayList<>();

    static {
        RRULES.add("");
        RRULES.add("FREQ=DAILY");
        RRULES.add("FREQ=WEEKLY");
        RRULES.add("FREQ=WEEKLY;INTERVAL=2");
        RRULES.add("FREQ=MONTHLY");
        RRULES.add("FREQ=YEARLY");
        RRULES.add("");
    }

    String repeatRule;
    int hasAlarm;
    Repetition repetition = Repetition.NONE;
    String location;
    String notes;
    private List<CalendarReminder> reminders = new ArrayList<>();

    public FullCalendarEvent() {
        super();
    }

    public FullCalendarEvent(Cursor cursor) {
        super(cursor);
        String duration = cursor.getString(CalendarHelper.PROJECTION_DURATION_INDEX);
        Log.i("CalendarHelper", "read duration: " + duration);
        repeatRule = cursor.getString(CalendarHelper.PROJECTION_RRULE_INDEX);
        hasAlarm = cursor.getInt(CalendarHelper.PROJECTION_HASALARM_INDEX);
        location = cursor.getString(CalendarHelper.PROJECTION_LOCATION_INDEX);
        notes = cursor.getString(CalendarHelper.PROJECTION_DESCRIPTION_INDEX);

        if (repeatRule == null || repeatRule.isEmpty()) {
            repetition = Repetition.NONE;
        } else {
            int ruleIndex = RRULES.indexOf(repeatRule);
            if (ruleIndex >= 0) {
                repetition = Repetition.values()[ruleIndex];
            } else {
                repetition = Repetition.CUSTOM;
            }
        }
    }

    public List<CalendarReminder> getReminders() {
        return reminders;
    }

    public void setReminders(List<CalendarReminder> reminders) {
        this.reminders = reminders;
    }

    public Repetition getRepeatMode() {
        return repetition;
    }

    public void setRepeatMode(Repetition repetition) {
        this.repetition = repetition;
        repeatRule = RRULES.get(repetition.ordinal());
    }

    public int getHasAlarm() {
        return hasAlarm;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public enum Repetition {
        NONE,
        DAILY,
        WEEKLY,
        BIWEEKLY,
        MONTHLY,
        YEARLY,
        CUSTOM,
    }

}

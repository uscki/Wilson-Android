package nl.uscki.appcki.android.helpers.calendar;

import android.database.Cursor;
import android.util.Log;

/**
 * Created by michielvanliempt on 10/01/15.
 */
public class FullCalendarEvent extends SimpleCalendarEvent {

    int hasAlarm;
    String location;
    String notes;

    public FullCalendarEvent() {
        super();
    }

    public FullCalendarEvent(Cursor cursor) {
        super(cursor);
        String duration = cursor.getString(CalendarHelper.PROJECTION_DURATION_INDEX);
        Log.i("CalendarHelper", "read duration: " + duration);
        hasAlarm = cursor.getInt(CalendarHelper.PROJECTION_HASALARM_INDEX);
        location = cursor.getString(CalendarHelper.PROJECTION_LOCATION_INDEX);
        notes = cursor.getString(CalendarHelper.PROJECTION_DESCRIPTION_INDEX);
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

}

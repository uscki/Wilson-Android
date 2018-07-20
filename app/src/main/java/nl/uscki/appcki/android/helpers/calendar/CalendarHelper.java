package nl.uscki.appcki.android.helpers.calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Pair;


import java.util.ArrayList;
import java.util.List;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;

import static android.provider.CalendarContract.Events;

public class CalendarHelper {

    private static CalendarHelper instance;
    private ContentResolver cr;
    private final long calendarId;


    private CalendarHelper() {
        Context context = App.getContext();
        cr = context.getContentResolver();
        calendarId = getPrimaryCalendarId();
    }

    /**
     * Get the CalendarHelper instance
     * @return CalendarHelper instance
     */
    public static CalendarHelper getInstance() {
        if (instance == null) {
            instance = new CalendarHelper();
        }
        return instance;
    }

    /***
     * Get the primary calendar used by the Android system
     *
     * @return Primary calendar ID if calendar read permissions are provided
     */
    public long getPrimaryCalendarId() {
        // TODO: Get this from settings
        return 0;
    }

    /**
     * Get a list of all available calendars on the device
     * @return  List of available calendar's (id / name pairs)
     */
    public ArrayList<Pair<String,String>> getCalendarList() {
        ArrayList<Pair<String,String>> calendars = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(
                App.getContext(), Manifest.permission.READ_CALENDAR
        ) != PackageManager.PERMISSION_GRANTED)  {
            return calendars;
        }

        String projection[] = {CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME};

        Uri calendarQuery;
        calendarQuery = CalendarContract.Calendars.CONTENT_URI;

        ContentResolver contentResolver;
        contentResolver = App.getContext().getContentResolver();

        Cursor cursor = contentResolver.query(calendarQuery, projection, null, null, null);
        if(cursor == null) return calendars;
        if(cursor.moveToFirst()) {
            int calendarIdIndex = cursor.getColumnIndex(projection[0]);
            int calendarNameIndex = cursor.getColumnIndex(projection[1]);

            do {
                calendars.add(
                        new Pair<>(
                                cursor.getString(calendarIdIndex),
                                cursor.getString(calendarNameIndex)
                        )
                );
            } while(cursor.moveToNext());
        }
        cursor.close();
        return calendars;
    }

    private void addEventViaProvider(AgendaItem item) {
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, item.getStart().getMillis());
        values.put(Events.DTEND, item.getEnd().getMillis());
        values.put(Events.TITLE, item.getTitle());
        values.put(Events.DESCRIPTION, item.getDescription());
        values.put(Events.CALENDAR_ID, calendarId);
        values.put(Events.CALENDAR_TIME_ZONE, "Europe/Amsterdam");
        values.put(Events.EVENT_LOCATION, item.getLocation());
        values.put(Events.GUESTS_CAN_INVITE_OTHERS, "0");
        values.put(Events.GUESTS_CAN_SEE_GUESTS, "0");
        if (ActivityCompat.checkSelfPermission(
                App.getContext(),
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        cr.insert(Events.CONTENT_URI, values);
    }

    private void addEventViaIntention(AgendaItem item) {
        // TODO does not require write permissions, but figure out how it works
    }


    public void addItemToCalendar(AgendaItem item) {
//// TODO: 5/15/16 IMPLEMENT
    }

    public void removeItemFromCalendar(AgendaItem item) {
        //TODO
    }

    public long addMeeting(MeetingItem item) {
//        FullCalendarEvent appointment = new FullCalendarEvent();
//        appointment.setTitle(item.getMeeting().getTitle());
//        appointment.setLocation(item.getMeeting().getLocation());
//        appointment.setNotes(item.getMeeting().getAgenda());
//        appointment.setStartDate(item.getMeeting().getStartdate());
//        appointment.setEndDate(item.getMeeting().getEnddate());
//        return appointment.getId();
        return -1L;
    }

    public List<CalendarReminder> getReminders() {
        return new ArrayList<>();
    }
}

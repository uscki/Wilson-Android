package nl.uscki.appcki.android.helpers.calendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityManagerCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.util.Pair;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.helpers.PermissionHelper;

import static android.provider.CalendarContract.Events;

public class CalendarHelper {

    private static CalendarHelper instance;
    private ContentResolver cr;

    private CalendarHelper() {
        Context context = App.getContext();
        cr = context.getContentResolver();
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

        ContentResolver contentResolver = App.getContext().getContentResolver();

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

    /**
     * Export agenda item to system calendar in background.
     * Original ID on the application server is stored in sync_data1, for
     * later comparison.
     *
     * @param item  Agenda item
     * @return False if not sufficient permissions are granted, true otherwise
     */
    private boolean addEventViaProvider(AgendaItem item) {
        if (ActivityCompat.checkSelfPermission(
                App.getContext(),
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        if(!preferences.getBoolean("calendar_use_export", false)) {
            return false;
        }

        String calendarId = preferences.getString("calendar_selected_id", "false");

        Log.e("UseCalendarID", calendarId);

        if(calendarId == "false") {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, item.getStart().getMillis());
        values.put(Events.DTEND, item.getEnd().getMillis());
        values.put(Events.TITLE, item.getTitle());
        values.put(Events.DESCRIPTION, item.getDescription());
        values.put(Events.CALENDAR_ID, calendarId);
        values.put(Events.EVENT_TIMEZONE, "Europe/Amsterdam");
        values.put(Events.EVENT_LOCATION, item.getLocation());
        values.put(Events.GUESTS_CAN_INVITE_OTHERS, "0");
        values.put(Events.GUESTS_CAN_SEE_GUESTS, "0");
        values.put(Events.ACCESS_LEVEL, Events.ACCESS_PUBLIC);
        values.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        values.put(Events.CUSTOM_APP_PACKAGE, "nl.uscki.appcki.android");
        values.put(Events.CUSTOM_APP_URI,
                String.format(Locale.ENGLISH,
                        "https://www.uscki.nl/?pagina=Agenda/Item&id=%d", item.getId()
                )
        );
        cr.insert(Events.CONTENT_URI, values);

        return true;
    }

    /**
     * Check if a given event already exists
     * @param item    Agenda Item to check
     * @return        True iff event exists in system calendar
     */
    public int AgendaItemExistsInCalendar(AgendaItem item) {
        long begin = item.getStart().getMillis();
        long end = item.getEnd().getMillis();

        if(!PermissionHelper.hasPermission(Manifest.permission.READ_CALENDAR))
            return -2;

        String[] projection =
                new String[] {
                        Events.DTSTART,
                        Events.DTEND,
                        Events.TITLE,
                        Events._ID
                };

        String where = String.format(
                "%s = ? AND %s = ? AND  %s = ?",
                Events.TITLE,
                Events.DTSTART,
                Events.DTEND
        );
        String[] arguments = new String[] {
                item.getTitle(),
                Long.toString(item.getStart().getMillis()),
                Long.toString(item.getEnd().getMillis())
        };

        ContentResolver contentResolver = App.getContext().getContentResolver();

        @SuppressLint("MissingPermission")
        Cursor cursor = contentResolver.query(
                Events.CONTENT_URI,
                projection,
                where,
                arguments,
                null);

        if(cursor == null) {
            Log.e("CheckEventExists", "Cursor is null");
            return -1;
        }

        Log.e("CheckEventExists", "Cursor is " + cursor.toString() + " (" + cursor.getCount() + " items)");

        while(cursor.moveToNext()) {
            long beginTime = cursor.getLong(cursor.getColumnIndex(projection[0]));
            long endTime = cursor.getLong(cursor.getColumnIndex(projection[1]));
            String eventTitle = cursor.getString(cursor.getColumnIndex(projection[2]));
            int eventId = cursor.getInt(cursor.getColumnIndex(projection[3]));
            Log.e("CheckEventExists", "Title: " + eventTitle + "\tBegin: " + beginTime + "\tEnd: " + endTime + "\tID: " + eventId);

            if(beginTime == begin && endTime == end && eventTitle.equals(item.getTitle())) {
                Log.e("CheckEventExists", "This event matches what we're looking for!");
                return eventId;
            }

        }
        Log.e("CheckEventExists", "No existing events found");
        return -1;
    }

    /**
     * Add an event to the system calendar when no write permissions
     * to calendar are given.
     * This opens the calendar app with all information filled out
     * @param item  Agenda item
     */
    private void addEventViaIntention(AgendaItem item) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, item.getStart().getMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, item.getEnd().getMillis());
        intent.putExtra(Events.TITLE, item.getTitle());
        intent.putExtra(Events.DESCRIPTION, item.getDescription() + "Just testing");
        intent.putExtra(Events.EVENT_TIMEZONE, "Europe/Amsterdam");
        intent.putExtra(Events.EVENT_LOCATION, item.getLocation());
        intent.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PUBLIC);
        intent.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        App.getContext().startActivity(intent);
    }

    public void addItemToCalendar(AgendaItem item) {
        if(!addEventViaProvider(item)) addEventViaIntention(item);
    }

    public boolean removeItemFromCalendar(AgendaItem item) {
        int eventId = AgendaItemExistsInCalendar(item);
        if(!PermissionHelper.hasPermission(Manifest.permission.WRITE_CALENDAR))
            return false;

        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);

        Log.e("Removing item", uri.toString());

        @SuppressLint("MissingPermission") // Checked through PermissionHelper
        int deleteCount = App.getContext().getContentResolver().delete(uri, null, null);

        Log.e("Removing item", "Removed " + deleteCount + " events with title '" + item.getTitle() + "' and id " + eventId);

        return deleteCount > 0;
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

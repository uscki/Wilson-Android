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
import android.util.Log;
import android.util.Pair;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.generated.meeting.Participation;
import nl.uscki.appcki.android.generated.meeting.Preference;
import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.bbparser.Parser;

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
    @SuppressLint("MissingPermission") // Performed using PermissionHelper
    private boolean addEventViaProvider(AgendaItem item) {
        if(!PermissionHelper.canExportCalendar()) {
            return false;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        String calendarId = preferences.getString("calendar_selected_id", "false");

        if(calendarId.equals("false")) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, item.getStart().getMillis());
        values.put(Events.DTEND, item.getEnd().getMillis());
        values.put(Events.TITLE, item.getTitle());
        values.put(Events.DESCRIPTION, getAgendaItemDescription(item));
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

    public int AgendaItemExistsInCalendar(AgendaItem item) {
        return AgendaItemExistsInCalendar(item.getTitle(), item.getStart().getMillis(), item.getEnd().getMillis());
    }

    public int AgendaItemExistsInCalendar(MeetingItem item) {
        if(item.getMeeting().getActual_slot() == null) return -1;
        return AgendaItemExistsInCalendar(
                item.getMeeting().getTitle(),
                item.getMeeting().getStartdate().getMillis(),
                item.getMeeting().getEnddate().getMillis());
    }

    /**
     * Check if a given event already exists in the system calendar
     * @param title        Title of the event
     * @param begin         Start time of the event
     * @param end          End time of the event
     * @return             ID of the event if found, -1 otherwise
     */
    private int AgendaItemExistsInCalendar(String title, long begin, long end) {
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
                title,
                Long.toString(begin),
                Long.toString(end)
        };

        ContentResolver contentResolver = App.getContext().getContentResolver();

        Log.e("FindingEventExists", "Looking for '" + title + "'\tbegin: " + begin + "\tend:" + end);

        @SuppressLint("MissingPermission")
        Cursor cursor = contentResolver.query(
                Events.CONTENT_URI,
                projection,
                where,
                arguments,
                null);

        if(cursor == null) {
            return -1;
        }


        while(cursor.moveToNext()) {
            long beginTime = cursor.getLong(cursor.getColumnIndex(projection[0]));
            long endTime = cursor.getLong(cursor.getColumnIndex(projection[1]));
            String eventTitle = cursor.getString(cursor.getColumnIndex(projection[2]));
            int eventId = cursor.getInt(cursor.getColumnIndex(projection[3]));
            Log.e("FindingEventExists", "Found '" + eventTitle + "'\tbegin: " + beginTime + "\tend:" + endTime + "\tid: " + eventId);
            if(beginTime == begin && endTime == end && eventTitle.equals(title)) {
                return eventId;
            }

        }
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
        intent.putExtra(Events.DESCRIPTION, getAgendaItemDescription(item));
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
        return removeItemFromCalendar(item.getTitle(), item.getStart().getMillis(), item.getEnd().getMillis());
    }

    public boolean removeItemFromCalendar(MeetingItem item) {
        return removeItemFromCalendar(item.getMeeting().getTitle(), item.getMeeting().getStartdate().getMillis(), item.getMeeting().getEnddate().getMillis());
    }

    private boolean removeItemFromCalendar(String title, long begin, long end) {
        int eventId = AgendaItemExistsInCalendar(title, begin, end);
        if(eventId < 0)
            return false;
        if(!PermissionHelper.hasPermission(Manifest.permission.WRITE_CALENDAR))
            return false;

        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);

        @SuppressLint("MissingPermission") // Checked through PermissionHelper
        int deleteCount = App.getContext().getContentResolver().delete(uri, null, null);

        return deleteCount > 0;
    }

    @SuppressLint("MissingPermission") // Performed using permission helper
    private boolean exportMeetingViaProvider(MeetingItem item) {
        if(!PermissionHelper.canExportCalendar()) {
            return false;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        String calendarId = preferences.getString("calendar_selected_id", "false");

        if(calendarId.equals("false")) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, item.getMeeting().getStartdate().getMillis());
        values.put(Events.DTEND, item.getMeeting().getEnddate().getMillis());
        values.put(Events.TITLE, item.getMeeting().getTitle());
        values.put(Events.DESCRIPTION, getMeetingDescription(item));
        values.put(Events.CALENDAR_ID, calendarId);
        values.put(Events.EVENT_TIMEZONE, "Europe/Amsterdam");
        values.put(Events.EVENT_LOCATION, item.getMeeting().getLocation());
        values.put(Events.GUESTS_CAN_INVITE_OTHERS, "0");
        values.put(Events.GUESTS_CAN_SEE_GUESTS, "0");
        values.put(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
        values.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        values.put(Events.CUSTOM_APP_PACKAGE, "nl.uscki.appcki.android");
        values.put(Events.CUSTOM_APP_URI,
                String.format(Locale.ENGLISH,
                        "https://www.uscki.nl/?pagina=MeetingPlanner/ShowMeeting&meeting_id=%d", item.getMeeting().getId()
                )
        );

        cr.insert(Events.CONTENT_URI, values);

        return true;
    }

    private void exportMeetingViaIntention(MeetingItem item) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, item.getMeeting().getStartdate().getMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, item.getMeeting().getEnddate().getMillis());
        intent.putExtra(Events.TITLE, item.getMeeting().getTitle());
        intent.putExtra(Events.DESCRIPTION, getMeetingDescription(item));
        intent.putExtra(Events.EVENT_TIMEZONE, "Europe/Amsterdam");
        intent.putExtra(Events.EVENT_LOCATION, item.getMeeting().getLocation());
        intent.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
        intent.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        App.getContext().startActivity(intent);
    }

    public void addMeeting(MeetingItem item) {
        if(!exportMeetingViaProvider(item)) exportMeetingViaIntention(item);
    }

    public List<CalendarReminder> getReminders() {
        return new ArrayList<>();
    }

    private String getAgendaItemDescription(AgendaItem item) {
        StringBuilder description = new StringBuilder();
        description.append(Parser.parseToHTML(item.getDescriptionJSON(), true));
        description.append("<br/>");
        description.append("<br/><b>Wie</b>: " + item.getWho());
        description.append("<br/><b>Wat</b>: " + item.getWhat());
        description.append("<br/><b>Waar</b>: " + item.getLocation());
        description.append("<br/><b>Wanneer</b>: " + item.getWhen());
        description.append("<br/><b>Kosten</b>: " + item.getCosts());

        return description.toString();
    }

    private String getMeetingDescription(MeetingItem item) {
        StringBuilder descriptionBuilder = new StringBuilder();
        if(!item.getMeeting().getAgenda().isEmpty()) {
            descriptionBuilder.append("<h3>Agenda</h3>");
            descriptionBuilder.append("<p><pre>" + item.getMeeting().getAgenda() + "</pre></p>");
        }
        if(!item.getMeeting().getNotes().isEmpty() || !item.getMeeting().getPlannotes().isEmpty()) {
            descriptionBuilder.append("<h3>Opmerkingen</h3>");
            if(!item.getMeeting().getNotes().isEmpty())
                descriptionBuilder.append("<p>" + item.getMeeting().getNotes() + "</p>");
            if(!item.getMeeting().getPlannotes().isEmpty())
                descriptionBuilder.append("<p>" + item.getMeeting().getPlannotes() + "</p>");
        }
        return descriptionBuilder.toString();
    }
}

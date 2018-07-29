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
import java.util.Locale;

import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
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
     *
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
     *
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
     * Verify if a certain calendar event already exists in the system calendar
     *
     * @param item  Agenda item to look for
     *
     * @return      ID of the event if found, -1 otherwise
     * @throws SecurityException if no read permissions are granted for calendar
     */
    public int getEventIdForItemIfExists(AgendaItem item) throws SecurityException{
        return getEventIdForItemIfExists(
                item.getTitle(),
                item.getStart().getMillis(),
                item.getEnd().getMillis()
        );
    }

    /**
     * Verify if a certain calendar event already exists in the system calendar
     *
     * @param item  Meeting item to look for
     *
     * @return      ID of the event if found, -1 otherwise
     * @throws SecurityException if no read permissions are granted for calendar
     */
    public int getEventIdForItemIfExists(MeetingItem item) throws SecurityException {
        if(item.getMeeting().getActual_slot() == null) return -1;

        return getEventIdForItemIfExists(
                item.getMeeting().getTitle(),
                item.getMeeting().getStartdate().getMillis(),
                item.getMeeting().getEnddate().getMillis());
    }

    /**
     * Check if a given event already exists in the system calendar
     *
     * @param title        Title of the event
     * @param begin         Start time of the event
     * @param end          End time of the event
     *
     * @return             ID of the event if found, -1 otherwise
     * @throws SecurityException if no read permissions are granted for calendar
     */
    private int getEventIdForItemIfExists(String title, long begin, long end) throws SecurityException {
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
            int eventId = cursor.getInt(cursor.getColumnIndex(projection[3]));
            long beginTime = cursor.getLong(cursor.getColumnIndex(projection[0]));
            long endTime = cursor.getLong(cursor.getColumnIndex(projection[1]));
            String eventTitle = cursor.getString(cursor.getColumnIndex(projection[2]));
            if(beginTime == begin && endTime == end && eventTitle.equals(title)) {
                return eventId;
            }

        }

        return -1;
    }

    /**
     * Add an agenda item to the system calendar. Method is automatically chosen based on
     * permissions granted to this application by the user
     *
     * @param item  Agenda item to export
     */
    public void addItemToCalendar(AgendaItem item) {
        if(!addAgendaEventViaProvider(item)) addAgendaEventViaIntention(item);
    }

    /**
     * Add a meeting item to the system calendar. Method is automatically chosen based on
     * permissions granted to this application by the user
     *
     * @param item  Meeting item to export
     */
    public void addItemToCalendar(MeetingItem item) {
        if(!exportMeetingViaProvider(item)) exportMeetingViaIntention(item);
    }

    /**
     * Store agenda item in system calendar
     *
     * @param item  Agenda item
     * @return True iff event was saved successfully
     */
    @SuppressLint("MissingPermission") // Performed using PermissionHelper
    private boolean addAgendaEventViaProvider(AgendaItem item) {
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

    /**
     * Add an event to the system calendar when no write permissions
     * to calendar are given.
     * This opens the calendar app with all information filled out
     *
     * @param item  Agenda item
     */
    private void addAgendaEventViaIntention(AgendaItem item) {
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

    /**
     * Export a meeting event through the provider. This function only works if both readning and
     * writing permissions on the system calendar are granted by the user
     *
     * @param item      Meeting item to export
     *
     * @return          True iff event was exported successfully
     */
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

    /**
     * Export a meeting item through an intention. This opens a calendar application with event
     * information filled out automatically, but saving the event is left to the user.
     * Useful if no reading and writing permissions for the system calendar are granted.
     *
     * @param item  The meeting item to export
     */
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

    /**
     * Remove an agenda item from the system calendar. This method does not perform any action
     * if insufficient permissions are granted
     *
     * @param item      Agenda item to remove from system calendar
     *
     * @return          Boolean indicating success status, true iff success
     */
    public boolean removeItemFromCalendar(AgendaItem item) {
        return removeItemFromCalendar(item.getTitle(), item.getStart().getMillis(), item.getEnd().getMillis());
    }

    /**
     * Remove a meeting item from the system calendar. This method does not perform any action
     * if insufficient permissions are granted
     *
     * @param item      Meeting item to remove from system calendar
     *
     * @return          Boolean indicating success status, true iff success
     */
    public boolean removeItemFromCalendar(MeetingItem item) {
        return removeItemFromCalendar(
                item.getMeeting().getTitle(),
                item.getMeeting().getStartdate().getMillis(),
                item.getMeeting().getEnddate().getMillis()
        );
    }

    /**
     * Search for a calendar event by title and start and end times. Remove this item if it can be found
     *
     * @param title        Title of the event to remove
     * @param begin        Start time of the event to remove in milliseconds
     * @param end          End time of the event to remove in milliseconds
     *
     * @return             Boolean indicating success status
     */
    private boolean removeItemFromCalendar(String title, long begin, long end) {
        int eventId;

        try {
            eventId = getEventIdForItemIfExists(title, begin, end);
        } catch(SecurityException e) {
            return false;
        }

        if(eventId < 0)
            return false;

        if(!PermissionHelper.hasPermission(Manifest.permission.WRITE_CALENDAR))
            return false;

        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);

        @SuppressLint("MissingPermission") // Checked through PermissionHelper
        int deleteCount = App.getContext().getContentResolver()
                .delete(uri, null, null);

        return deleteCount > 0;
    }

    /**
     * Construct an HTML markup string with the description of an agenda event
     *
     * @param item      Agenda event
     *
     * @return          String containing a bit of HTML and a bit of event description
     */
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

    /**
     * Construct an HTML markup string with the description of a meeting event
     *
     * @param item      Meeting event
     *
     * @return          String containing a bit of HTML and a bit of event description
     */
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

package me.blackwolf12333.appcki.helpers.calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import me.blackwolf12333.appcki.App;
import me.blackwolf12333.appcki.generated.agenda.AgendaItem;

import static android.provider.CalendarContract.Events;
import static android.provider.CalendarContract.Instances;

/**
 * Created by michielvanliempt on 08/01/15.
 */
public class CalendarHelper {

    static final String TAG = "CalendarHelper";

    public static final String[] CALENDAR_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_TYPE,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.NAME,                  // 3
            CalendarContract.Calendars.CALENDAR_COLOR
    };

    // The indices for the projection array above.
    static final int CALENDAR_PROJECTION_ID_INDEX = 0;
    static final int CALENDAR_PROJECTION_ACCOUNT_TYPE_INDEX = 1;
    static final int CALENDAR_PROJECTION_DISPLAY_NAME_INDEX = 2;
    static final int CALENDAR_PROJECTION_NAME_INDEX = 3;
    static final int CALENDAR_PROJECTION_COLOR_INDEX = 4;

    public static final String[] INSTANCE_PROJECTION = new String[]{
            Instances.BEGIN,         // 1
            Instances.END            // 2
    };

    public static final String[] EVENT_PROJECTION_SIMPLE = new String[]{
            Events._ID,      // 0
            Events.DTSTART,         // 1
            Events.TITLE,          // 2
            Events.DTEND,
            Events.ALL_DAY,   // 4
    };

    public static final String[] EVENT_PROJECTION_FULL = new String[]{
            Events._ID,       // 0
            Events.DTSTART,   // 1
            Events.TITLE,     // 2
            Events.DTEND,     // 3
            Events.ALL_DAY,   // 4
            Events.RRULE,     // 5
            Events.HAS_ALARM, // 6
            Events.DESCRIPTION,// 7
            Events.EVENT_LOCATION, // 8
            Events.DURATION,
    };

    // The indices for the projection array above.
    static final int PROJECTION_ID_INDEX = 0;
    static final int PROJECTION_BEGIN_INDEX = 1;
    static final int PROJECTION_TITLE_INDEX = 2;
    static final int PROJECTION_END_INDEX = 3;
    static final int PROJECTION_ALLDAY_INDEX = 4;
    static final int PROJECTION_RRULE_INDEX = 5;
    static final int PROJECTION_HASALARM_INDEX = 6;
    static final int PROJECTION_DESCRIPTION_INDEX = 7;
    static final int PROJECTION_LOCATION_INDEX = 8;
    static final int PROJECTION_DURATION_INDEX = 9;
    private static final String[] REMINDER_PROJECTION = {CalendarContract.Reminders.MINUTES};

    private final long calendarId;
    private static CalendarHelper instance;
    private final Context context;
    private ContentResolver cr;

    public long getPrimaryCalendarId() {
        Cursor cur = null;
        Cursor calendarCursor = null;
        Long id = -1L;

        try {
            String selection = CalendarContract.Calendars.ACCOUNT_TYPE + " = 'com.google' AND " + CalendarContract.Calendars.VISIBLE + " = 1";
            calendarCursor = cr.query(CalendarContract.Calendars.CONTENT_URI, CALENDAR_PROJECTION, selection, null, null);

            if (calendarCursor.getCount() > 0) {
                if (calendarCursor.moveToFirst()) {
                    id = calendarCursor.getLong(CALENDAR_PROJECTION_ID_INDEX);
                    String accountType = calendarCursor.getString(CALENDAR_PROJECTION_ACCOUNT_TYPE_INDEX);
                    String displayName = calendarCursor.getString(CALENDAR_PROJECTION_DISPLAY_NAME_INDEX);
                    String userName = calendarCursor.getString(CALENDAR_PROJECTION_NAME_INDEX);
                    int color = calendarCursor.getInt(CALENDAR_PROJECTION_COLOR_INDEX);
                    Log.i("appointment", String.format("%d %s %s %s", id, userName, displayName, accountType));
                    return id;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } finally {
            calendarCursor.close();
        }
        return id;
    }

    public static CalendarHelper getInstance() {
        if (instance == null) {
            instance = new CalendarHelper();
        }
        return instance;
    }

    public SimpleCalendarEvent getEventSimple(long id) {

        Uri uri = Events.CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        Cursor cur = cr.query(uri, EVENT_PROJECTION_SIMPLE, null, null, Events.DTSTART);
        try {
            if (cur.moveToNext()) {
                return new SimpleCalendarEvent(cur);
            }
        } finally {
            cur.close();
        }
        return null;
    }

    public FullCalendarEvent getEventFull(long id) {

        Uri uri = Events.CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        Cursor cur = cr.query(uri, EVENT_PROJECTION_FULL, null, null, null);
        FullCalendarEvent calendarEvent = null;
        try {
            if (cur.moveToNext()) {
                calendarEvent = new FullCalendarEvent(cur);
            }
        } finally {
            cur.close();
        }

        if (calendarEvent != null) {
            // get end date from instances table, beacuse the calendarprovider is weird with dtend and duration
            long endMillis = getEventInstanceEndTime(calendarEvent.getId(), calendarEvent.startDate);
            if (endMillis > 0) {
                calendarEvent.endDate = new DateTime(endMillis);
            }

            // retrieve the list of reminders
            List<CalendarReminder> reminderList = new ArrayList<>();

            try {
                cur = cr.query(CalendarContract.Reminders.CONTENT_URI, REMINDER_PROJECTION, makeReminderSelect(id), null, null);
                while (cur.moveToNext()) {
                    reminderList.add(new CalendarReminder(cur.getInt(0)));
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } finally {
                cur.close();
            }
            calendarEvent.setReminders(reminderList);
        }

        return calendarEvent;
    }

    private long getEventInstanceEndTime(long id, DateTime startDate) {
        Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startDate.withTimeAtStartOfDay().getMillis());
        ContentUris.appendId(builder, startDate.withTimeAtStartOfDay().plusDays(1).getMillis());
        String selection = Instances.EVENT_ID + " = ?";
        Cursor cur = cr.query(builder.build(), INSTANCE_PROJECTION, selection, new String[]{Long.toString(id)}, null);
        try {
            if (cur.moveToNext()) {
                return cur.getLong(1);
            }
        } finally {
            cur.close();
        }
        return 0;
    }

    /**
     * voorbeeld usage
     *if (appointment == null) {
     appointment = new FullCalendarEvent();
     }
     appointment.setTitle(titleView.getText().toString());
     appointment.setLocation(locationView.getText().toString());
     appointment.setNotes(notesView.getText().toString());
     appointment.setStartDate(startDate);
     appointment.setEndDate(endDate);
     List<CalendarReminder> reminders = new ArrayList<>();
     for (int i = 0; i < reminderListView.getChildCount(); i++) {
     View reminder = reminderListView.getChildAt(i);
     reminders.add((CalendarReminder) reminder.getTag(R.id.reminders));
     }
     appointment.setReminders(reminders);
     boolean newAppointment = appointment.getId() < 0;
     helper.insertOrUpdate(appointment);
     if (newAppointment) {
     EventHelper.getInstance().addAppointmentEvent(appointment.getId());
     }
     *
     * @param appointment
     */
    public void insertOrUpdate(FullCalendarEvent appointment) {
        ContentValues values = new ContentValues();
        long startMillis = appointment.getStartDate().getMillis();
        long endMillis = appointment.getEndDate().getMillis();
        values.put(Events.DTSTART, startMillis);
        values.put(Events.TITLE, appointment.getTitle());
        values.put(Events.CALENDAR_ID, calendarId);
        values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        values.put(Events.ALL_DAY, appointment.isAllDay);
        values.put(Events.EVENT_LOCATION, appointment.location);
        values.put(Events.DESCRIPTION, appointment.notes);
        if (appointment.getRepeatMode() == FullCalendarEvent.Repetition.NONE) {
            values.put(Events.DTEND, endMillis);
            values.put(Events.RRULE, (String) null);
            values.put(Events.DURATION, (String) null);
        } else {
            values.put(Events.RRULE, appointment.repeatRule);
            String dateString = appointment.startDate.toString(DateTimeFormat.forPattern("YYYYMMdd-HHmmss"));
            dateString = dateString.replace("-", "T"); // can't put T in the pattern
            int seconds = (int) ((endMillis - startMillis) / 1000);
            if (appointment.isAllDay()) {
                // todo really use days in stead of seconds here
                values.put(Events.DURATION, String.format("P%dD", seconds / 86400)); // Server wants this instead of P86400S
            } else {
                values.put(Events.DURATION, String.format("P%dS", seconds));
            }

            values.put(Events.DTEND, (Long) null);
        }

        long id = appointment.getId();
        if (id == -1) {
            try {
                Uri uri = cr.insert(Events.CONTENT_URI, values);
                appointment.id = Long.parseLong(uri.getLastPathSegment());
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            Uri uri = Uri.withAppendedPath(Events.CONTENT_URI, Long.toString(id));
            cr.update(uri, values, null, null);
            deleteReminders(id);
        }
        try {
            addReminders(id, appointment.getReminders());
        } catch (Exception e) {
            Log.e(TAG, "ignored exception in addReminders");
            e.printStackTrace();
        }
    }

    public void delete(SimpleCalendarEvent appointment) {
        long id = appointment.getId();
        delete(id);
    }

    public void delete(Long appointmentId) {
        Uri uri = Uri.withAppendedPath(Events.CONTENT_URI, Long.toString(appointmentId));
        cr.delete(uri, null, null);
        deleteReminders(appointmentId);
    }


    private void deleteReminders(long id) {
        try {
            cr.delete(CalendarContract.Reminders.CONTENT_URI, makeReminderSelect(id), null);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private String makeReminderSelect(long id) {
        return CalendarContract.Reminders.EVENT_ID + " = " + Long.toString(id);
    }

    private void addReminders(long id, List<CalendarReminder> reminders) {
        try {
            ContentValues values = new ContentValues();
            for (CalendarReminder reminder : reminders) {
                values.put(CalendarContract.Reminders.EVENT_ID, id);
                values.put(CalendarContract.Reminders.MINUTES, reminder.getMinutes());
                values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_DEFAULT);
                cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void addItemToCalendar(AgendaItem item) {
//// TODO: 5/15/16 IMPLEMENT
    }

    public void removeItemFromCalendar(AgendaItem item) {
        //TODO
    }

    public List<CalendarReminder> getReminders() {
        return new ArrayList<>();
    }

    private CalendarHelper() {
        context = App.getContext();
        cr = context.getContentResolver();
        calendarId = getPrimaryCalendarId();
    }

}

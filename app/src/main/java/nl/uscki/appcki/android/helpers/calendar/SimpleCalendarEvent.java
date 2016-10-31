package nl.uscki.appcki.android.helpers.calendar;

import android.database.Cursor;

import org.joda.time.DateTime;

/**
* Created by michielvanliempt on 08/01/15.
*/
public class SimpleCalendarEvent {
    String title;
    DateTime startDate;
    long id = -1;
    DateTime endDate;
    int isAllDay;

    public SimpleCalendarEvent(Cursor cursor) {
        id = cursor.getLong(CalendarHelper.PROJECTION_ID_INDEX);
        long beginVal = cursor.getLong(CalendarHelper.PROJECTION_BEGIN_INDEX);
        startDate = new DateTime(beginVal);
        title = cursor.getString(CalendarHelper.PROJECTION_TITLE_INDEX);
        long endVal = cursor.getLong(CalendarHelper.PROJECTION_END_INDEX);
        endDate = new DateTime(endVal);
        isAllDay = cursor.getInt(CalendarHelper.PROJECTION_ALLDAY_INDEX);
    }

    public SimpleCalendarEvent() {
        startDate = DateTime.now().withMinuteOfHour(0).withSecondOfMinute(0).plusHours(1);
        title = "";
        endDate = startDate.plusHours(1);
    }

    public SimpleCalendarEvent(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "";//String.format("%s\n%d %s", title, id, startDate.toString(Utils.FORMATTER_DATETIME));
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isAllDay() {
        return isAllDay != 0;
    }

    public void setAllDay(boolean isAllDay) {
        this.isAllDay = isAllDay ? 1 : 0;
    }
}

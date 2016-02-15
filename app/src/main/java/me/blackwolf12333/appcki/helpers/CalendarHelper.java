package me.blackwolf12333.appcki.helpers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import me.blackwolf12333.appcki.generated.agenda.AgendaItem;

/**
 * Created by peter on 2/7/16.
 */
public class CalendarHelper {
    private static CalendarHelper instance;
    private Context context;
    private HashMap<Integer, Long> eventsMap = new HashMap<>();

    private CalendarHelper(Context context) {
        this.context = context;
    }

    public static CalendarHelper getInstance(Context context) {
        if(instance == null) {
            instance = new CalendarHelper(context);
        }
        return instance;
    }

    public void addItemToCalendar(AgendaItem item) {
        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setData(CalendarContract.Events.CONTENT_URI);
        calIntent.putExtra(CalendarContract.Events.TITLE, item.getWhat());
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, item.getWhere());
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, item.getShortdescription());

        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            Date date = format.parse(item.getStartdate() + " " + item.getStarttime());
            calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                    date.getTime());

            // als de enddate NULL is in de database is de enddate dezelfde dag als de startdate
            if(item.getEnddate() == null || item.getEnddate().isEmpty()) {
                date = format.parse(item.getStartdate() + " " + item.getEndtime());
            } else {
                date = format.parse(item.getEnddate() + " " + item.getEndtime());
            }

            calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                    date.getTime());

            Uri uri = calIntent.getData();
            Log.d("CalendarHelper", uri.toString());
            long eventID = Long.parseLong(uri.getLastPathSegment());
            eventsMap.put(item.getId(), eventID);
        } catch (SecurityException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }

        context.startActivity(calIntent);
    }

    public void removeItemFromCalendar(AgendaItem item) {
        //Intent calIntent = new Intent(Intent.ACTION_DELETE);
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        Uri deleteUri = null;
        Long eventID = eventsMap.get(item.getId());
        if(eventID != null) {
            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
            int rows = cr.delete(deleteUri, null, null);
            Log.i("CalendarHelper", "Rows deleted: " + rows);
        }
    }
}

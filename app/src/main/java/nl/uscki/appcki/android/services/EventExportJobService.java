package nl.uscki.appcki.android.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import nl.uscki.appcki.android.helpers.calendar.CalendarServiceHelper;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests for downloading
 * items from the server and storing them in device applications, such as calendar or people
 */
public class EventExportJobService extends JobIntentService {

    // Must be unique for scheduled jobs, but always used for this class
    public static final int EVENT_EXPORT_JOB_ID = 10005;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.e(getClass().getSimpleName(), "Work scheduled!");

        final String action = intent.getAction();

        CalendarServiceHelper csh = new CalendarServiceHelper(this);

        if (CalendarServiceHelper.ACTION_AGENDA_EXPORT.equals(action)) {
            final int id = intent.getIntExtra(CalendarServiceHelper.PARAM_AGENDA_ID, -1);
            csh.handleAgendaExportIntent(id);
        } else if (CalendarServiceHelper.ACTION_MEETING_EXPORT.equals(action)) {
            int id = intent.getIntExtra(CalendarServiceHelper.PARAM_MEETING_ID, -3);
            if(id < 0) id = intent.getIntExtra("id", -2);
            csh.handleMeetingExportIntent(id);
        }
    }

    /**
     * Starts this service to perform export agenda event action.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void enqueueExportAgendaToCalendarAction(Context context, int agendaId) {
        Intent intent = new Intent(context, EventExportJobService.class);
        intent.setAction(CalendarServiceHelper.ACTION_AGENDA_EXPORT);
        intent.putExtra(CalendarServiceHelper.PARAM_AGENDA_ID, agendaId);

        // JobIntentService is enqueued in oreo+ devices, but starts using context.startService
        // on pre-Oreo devices
        EventExportJobService.enqueueWork(context, EventExportJobService.class, EVENT_EXPORT_JOB_ID, intent);
    }

    /**
     * Starts this service to perform export agenda event action.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void enqueueExportMeetingToCalendarAction(Context context, int meetingId) {
        Intent intent = new Intent(context, EventExportJobService.class);
        intent.setAction(CalendarServiceHelper.ACTION_MEETING_EXPORT);
        intent.putExtra(CalendarServiceHelper.PARAM_MEETING_ID, meetingId);

        // JobIntentService is enqueued in oreo+ devices, but starts using context.startService
        // on pre-Oreo devices
        EventExportJobService.enqueueWork(context, EventExportJobService.class, EVENT_EXPORT_JOB_ID, intent);
    }

}

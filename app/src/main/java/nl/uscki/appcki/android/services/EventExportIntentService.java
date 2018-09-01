package nl.uscki.appcki.android.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import nl.uscki.appcki.android.helpers.calendar.CalendarServiceHelper;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class EventExportIntentService extends IntentService {

    public EventExportIntentService() {
        super("EventExportIntentService");
    }

    /**
     * Starts this service to perform export agenda event action.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startExportAgendaToCalendarAction(Context context, int agendaId) {
        Intent intent = new Intent(context, EventExportIntentService.class);
        intent.setAction(CalendarServiceHelper.ACTION_AGENDA_EXPORT);
        intent.putExtra(CalendarServiceHelper.PARAM_AGENDA_ID, agendaId);
        context.startService(intent);
    }

    /**
     * Starts this service to perform export agenda event action.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startExportMeetingToCalendarAction(Context context, int meetingId) {
        Intent intent = new Intent(context, EventExportIntentService.class);
        intent.setAction(CalendarServiceHelper.ACTION_MEETING_EXPORT);
        intent.putExtra(CalendarServiceHelper.PARAM_MEETING_ID, meetingId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
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
    }
}

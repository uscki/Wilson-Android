package nl.uscki.appcki.android.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.ServiceGenerator;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.helpers.calendar.CalendarHelper;
import retrofit2.Response;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests for downloading
 * items from the server and storing them in device applications, such as calendar or people
 */
public class EventExportService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_AGENDA_EXPORT = "nl.uscki.appcki.android.services.action.AGENDA_EXPORT";
    public static final String ACTION_MEETING_EXPORT = "nl.uscki.appcki.android.services.action.MEETING_EXPORT";

    public static final String PARAM_AGENDA_ID = "nl.uscki.appcki.android.services.param.AGENDA_ID";
    public static final String PARAM_MEETING_ID = "nl.uscki.appcki.android.services.param.MEETING_ID";

    public EventExportService() {
        super("EventExportService");
    }

    /**
     * Starts this service to perform export agenda event action.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startExportAgendaToCalendarAction(Context context, int agendaId) {
        Intent intent = new Intent(context, EventExportService.class);
        intent.setAction(ACTION_AGENDA_EXPORT);
        intent.putExtra(PARAM_AGENDA_ID, agendaId);
        context.startService(intent);
    }

    /**
     * Starts this service to perform export agenda event action.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startExportMeetingToCalendarAction(Context context, int meetingId) {
        Intent intent = new Intent(context, EventExportService.class);
        intent.setAction(ACTION_MEETING_EXPORT);
        intent.putExtra(PARAM_MEETING_ID, meetingId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(this.toString(), "Received intent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_AGENDA_EXPORT.equals(action)) {
                final int id = intent.getIntExtra(PARAM_AGENDA_ID, -1);
                handleAgendaExportIntent(id);
            } else if (ACTION_MEETING_EXPORT.equals(action)) {
                int id = intent.getIntExtra(PARAM_MEETING_ID, -3);
                if(id < 0) id = intent.getIntExtra("id", -2);
                handleMeetingExportIntent(id);
            }
        }
    }

    /**
     * Perform initialization of API, including adding a token to headers
     */
    private void init() {
        ServiceGenerator.init();
        UserHelper.getInstance().load();
    }

    /**
     * Download the agenda event and store it in the device calendar
     * @param id    Agenda event ID
     */
    private void handleAgendaExportIntent(int id) {
        if(id < 0) return;
        init();
        Services.getInstance().agendaService.get(id).enqueue(new Callback<AgendaItem>() {
            @Override
            public void onSucces(Response<AgendaItem> response) {
                AgendaItem item = response.body();
                if(item != null) {

                    int calendarEventItemId;

                    try {
                        calendarEventItemId = CalendarHelper.getInstance()
                                .getEventIdForItemIfExists(item);
                    } catch(SecurityException e) {
                        calendarEventItemId = -1;
                    }

                    if(calendarEventItemId < 0) {
                        // If it already exists, pretend we just added it anyway
                        CalendarHelper.getInstance().addItemToCalendar(item);
                    }
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.agenda_toast_added_to_calendar),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    /**
     * Download a meeting item and store it in the device calendar
     * @param id    Meeting id
     */
    private void handleMeetingExportIntent(int id) {
        if(id < 0) return;
        init();
        Services.getInstance().meetingService.get(id).enqueue(new Callback<MeetingItem>() {
            @Override
            public void onSucces(Response<MeetingItem> response) {
                MeetingItem item = response.body();
                if(item != null) {

                    int calendarEventItemId;

                    try {
                        calendarEventItemId = CalendarHelper.getInstance()
                                .getEventIdForItemIfExists(item);
                    } catch(SecurityException e) {
                        calendarEventItemId = -1;
                    }

                    if(calendarEventItemId < 0) {
                        // If it already exists, pretend we just added it anyway
                        CalendarHelper.getInstance().addItemToCalendar(item);
                    }
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.agenda_toast_added_to_calendar),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }
}

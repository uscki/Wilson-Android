package nl.uscki.appcki.android.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.ServiceGenerator;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.ServerError;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.helpers.calendar.CalendarHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests for downloading
 * items from the server and storing them in device applications, such as calendar or people
 */
public class EventExportService extends JobIntentService {

    // Must be unique for scheduled jobs, but always used for this class
    public static final int EVENT_EXPORT_JOB_ID = 10005;

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_AGENDA_EXPORT = "nl.uscki.appcki.android.services.action.AGENDA_EXPORT";
    public static final String ACTION_MEETING_EXPORT = "nl.uscki.appcki.android.services.action.MEETING_EXPORT";

    public static final String PARAM_AGENDA_ID = "nl.uscki.appcki.android.services.param.AGENDA_ID";
    public static final String PARAM_MEETING_ID = "nl.uscki.appcki.android.services.param.MEETING_ID";

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.e(getClass().getSimpleName(), "Work scheduled!");

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

    /**
     * Starts this service to perform export agenda event action.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void enqueueExportAgendaToCalendarAction(Context context, int agendaId) {
        Intent intent = new Intent(context, EventExportService.class);
        intent.setAction(ACTION_AGENDA_EXPORT);
        intent.putExtra(PARAM_AGENDA_ID, agendaId);

        // JobIntentService is enqueued in oreo+ devices, but starts using context.startService
        // on pre-Oreo devices
        EventExportService.enqueueWork(context, EventExportService.class, EVENT_EXPORT_JOB_ID, intent);
    }

    /**
     * Starts this service to perform export agenda event action.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void enqueueExportMeetingToCalendarAction(Context context, int meetingId) {
        Intent intent = new Intent(context, EventExportService.class);
        intent.setAction(ACTION_MEETING_EXPORT);
        intent.putExtra(PARAM_MEETING_ID, meetingId);

        // JobIntentService is enqueued in oreo+ devices, but starts using context.startService
        // on pre-Oreo devices
        EventExportService.enqueueWork(context, EventExportService.class, EVENT_EXPORT_JOB_ID, intent);
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
        Services.getInstance().agendaService.get(id).enqueue(new retrofit2.Callback<AgendaItem>() {
            @Override
            public void onResponse(Call<AgendaItem> call, Response<AgendaItem> response) {
                if(response.isSuccessful()) {
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

                        showToastForResource(R.string.agenda_toast_added_to_calendar);
                    }
                } else {
                    handleError(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<AgendaItem> call, Throwable t) {
                Log.e(getClass().toString(), t.getMessage());
                showToastForResource(R.string.unknown_server_error);
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
        Services.getInstance().meetingService.get(id).enqueue(new retrofit2.Callback<MeetingItem>() {
            @Override
            public void onResponse(Call<MeetingItem> call, Response<MeetingItem> response) {
                if(response.isSuccessful()) {
                    MeetingItem item = response.body();
                    if(item != null && item.getMeeting().getActual_slot() != null) {

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

                        showToastForResource(R.string.agenda_toast_added_to_calendar);
                    }
                } else {
                    handleError(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<MeetingItem> call, Throwable t) {
                Log.e(getClass().toString(), t.getMessage());
                showToastForResource(R.string.unknown_server_error);
            }
        });
    }

    private void handleError(ResponseBody body) {
        try {
            Gson gson = new Gson();
            ServerError error = gson.fromJson(body.string(), ServerError.class);
            handleStatusCode(error.getStatus());
        } catch(Exception e) {
            Log.e(getClass().toString(), e.toString());
            showToastForResource(R.string.unknown_server_error);
        }
    }

    private void handleStatusCode(int statusCode) {
        int resourceId = R.string.connection_error;
        if(statusCode == 401) {
            resourceId = R.string.notauthorized;
        } else if(statusCode == 403) {
            resourceId = R.string.notloggedin;
        } else if(statusCode == 404) {
            resourceId = R.string.content_loading_error;
        } else if (statusCode == 500) {
            resourceId = R.string.unknown_server_error;
        }

        showToastForResource(resourceId);
    }

    private void showToastForResource(int resourceId) {
        Toast toast = Toast.makeText(
                this,
                getString(resourceId),
                Toast.LENGTH_SHORT
        );
        toast.show();
    }
}
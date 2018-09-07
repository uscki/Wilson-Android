package nl.uscki.appcki.android.helpers.calendar;

import android.content.Context;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Helper class for calendar related services, containing common fields and methods
 *
 * Since android oreo, services started from the background have to be job services, but starting
 * a job service from a pending intent introduces heavy delays. This requires to seperate services
 * that do roughly the same work.
 */
public class CalendarServiceHelper {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_AGENDA_EXPORT = "nl.uscki.appcki.android.services.action.AGENDA_EXPORT";
    public static final String ACTION_MEETING_EXPORT = "nl.uscki.appcki.android.services.action.MEETING_EXPORT";

    public static final String PARAM_AGENDA_ID = "nl.uscki.appcki.android.services.param.AGENDA_ID";
    public static final String PARAM_MEETING_ID = "nl.uscki.appcki.android.services.param.MEETING_ID";

    private Context context;

    public CalendarServiceHelper(Context context) {
        this.context = context;
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
    public void handleAgendaExportIntent(int id) {
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
    public void handleMeetingExportIntent(int id) {
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

    public void handleError(ResponseBody body) {
        try {
            Gson gson = new Gson();
            ServerError error = gson.fromJson(body.string(), ServerError.class);
            handleStatusCode(error.getStatus());
        } catch(Exception e) {
            Log.e(getClass().toString(), e.toString());
            showToastForResource(R.string.unknown_server_error);
        }
    }

    public void handleStatusCode(int statusCode) {
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
                context,
                context.getString(resourceId),
                Toast.LENGTH_SHORT
        );
        toast.show();
    }

}

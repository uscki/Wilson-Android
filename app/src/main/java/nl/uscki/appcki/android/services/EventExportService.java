package nl.uscki.appcki.android.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.ServiceGenerator;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.generated.meeting.MeetingItem;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.helpers.calendar.CalendarHelper;
import retrofit2.Response;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class EventExportService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_AGENDA_EXPORT = "nl.uscki.appcki.android.services.action.AGENDA_EXPORT";
    public static final String ACTION_MEETING_EXPORT = "nl.uscki.appcki.android.services.action.MEETING_EXPORT";

    // TODO: Rename parameters
    public static final String PARAM_AGENDA_ID = "nl.uscki.appcki.android.services.param.AGENDA_ID";
    public static final String PARAM_MEETING_ID = "nl.uscki.appcki.android.services.param.MEETING_ID";

    public EventExportService() {
        super("EventExportService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(this.toString(), "Received intent");
        if (intent != null) {
            final String action = intent.getAction();
            Log.e(this.toString(), "Intent is not null, action is " + action);
            if (ACTION_AGENDA_EXPORT.equals(action)) {
                final int id = intent.getIntExtra(PARAM_AGENDA_ID, -1);
                handleAgendaExportIntent(id);
            } else if (ACTION_MEETING_EXPORT.equals(action)) {
                final int id = intent.getIntExtra(PARAM_MEETING_ID, -1);
                handleMeetingExportIntent(id);
            }
        }
    }

    private void init() {
        ServiceGenerator.init();
        UserHelper.getInstance().load();
    }

    private void handleAgendaExportIntent(int id) {
        Services.getInstance().agendaService.get(id).enqueue(new Callback<AgendaItem>() {
            @Override
            public void onSucces(Response<AgendaItem> response) {
                AgendaItem item = response.body();
                if(item != null) {
                    Log.e(this.toString(), "Agenda item received from server. Initiating export");
//                    CalendarHelper.getInstance().addItemToCalendar(item);
                }
            }
        });
    }

    private void handleMeetingExportIntent(int id) {
        if(id < 0) return;

        Services.getInstance().meetingService.get(id).enqueue(new Callback<MeetingItem>() {
            @Override
            public void onSucces(Response<MeetingItem> response) {
                MeetingItem item = response.body();
                if(item != null) {
                    Log.e(this.toString(), "Meeting item received from server. Initiating export");
//                    CalendarHelper.getInstance().addMeetingItemToCalendar(item);
                }
            }
        });
    }


}

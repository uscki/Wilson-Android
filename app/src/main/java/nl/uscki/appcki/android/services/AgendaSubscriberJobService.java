package nl.uscki.appcki.android.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import android.util.Log;

import nl.uscki.appcki.android.helpers.calendar.AgendaSubscribeServiceHelper;

/**
 * An {@link IntentService} subclass for subscribing users to an agenda event
 */
public class AgendaSubscriberJobService extends JobIntentService {

    // Must be unique for scheduled jobs, but always used for this class
    public static final int AGENDA_SUBSCRIBE_JOB_ID = 10006;

    /**
     * Starts this service to perform action subscribe to agenda event action.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void enqueueSubscribeAction(Context context, int agendaId) {
        Intent intent = new Intent(context, AgendaSubscriberJobService.class);
        intent.setAction(AgendaSubscribeServiceHelper.ACTION_SUBSCRIBE_AGENDA);
        intent.putExtra(AgendaSubscribeServiceHelper.PARAM_AGENDA_ID, agendaId);

        // JobIntentService is enqueued in oreo+ devices, but starts using context.startService
        // on pre-Oreo devices
        EventExportJobService.enqueueWork(context, AgendaSubscriberJobService.class, AGENDA_SUBSCRIBE_JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        final String action = intent.getAction();

        AgendaSubscribeServiceHelper assh = new AgendaSubscribeServiceHelper(this);

        if (AgendaSubscribeServiceHelper.ACTION_SUBSCRIBE_AGENDA.equals(action)) {
            final int agendaId = intent.getIntExtra(AgendaSubscribeServiceHelper.PARAM_AGENDA_ID, -1);
            final String subscribeComment = assh.getSubscribeText(intent).toString();

            Log.e(this.toString(), "Found agenda id " + agendaId + " and comment '" + subscribeComment + "'");

            assh.handleAgendaSubscribeAction(agendaId, subscribeComment, intent);
        }
    }
}

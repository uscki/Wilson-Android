package nl.uscki.appcki.android.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import nl.uscki.appcki.android.helpers.calendar.AgendaSubscribeServiceHelper;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AgendaSubscriberIntentService extends IntentService {

    public AgendaSubscriberIntentService() {
        super("AgendaSubscriberIntentService");
    }

    /**
     * Starts this service to perform action subscribe to agenda event action.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startSubscribeAction(Context context, int agendaId) {
        Intent intent = new Intent(context, AgendaSubscriberIntentService.class);
        intent.setAction(AgendaSubscribeServiceHelper.ACTION_SUBSCRIBE_AGENDA);
        intent.putExtra(AgendaSubscribeServiceHelper.PARAM_AGENDA_ID, agendaId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            AgendaSubscribeServiceHelper assh = new AgendaSubscribeServiceHelper(this);

            if (AgendaSubscribeServiceHelper.ACTION_SUBSCRIBE_AGENDA.equals(action)) {
                final int agendaId = intent.getIntExtra(AgendaSubscribeServiceHelper.PARAM_AGENDA_ID, -1);
                final String subscribeComment = assh.getSubscribeText(intent).toString();
                assh.handleAgendaSubscribeAction(agendaId, subscribeComment, intent);
            }
        }
    }

}

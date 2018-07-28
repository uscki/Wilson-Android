package nl.uscki.appcki.android.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import nl.uscki.appcki.android.App;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.api.Callback;
import nl.uscki.appcki.android.api.ServiceGenerator;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.events.AgendaItemSubscribedEvent;
import nl.uscki.appcki.android.fragments.agenda.SubscribeDialogFragment;
import nl.uscki.appcki.android.generated.agenda.AgendaParticipantLists;
import nl.uscki.appcki.android.helpers.PermissionHelper;
import nl.uscki.appcki.android.helpers.UserHelper;
import retrofit2.Call;
import retrofit2.Response;

/**
 * An {@link IntentService} subclass for subscribing users to an agenda event
 */
public class AgendaSubscriberService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_SUBSCRIBE_AGENDA
            = "nl.uscki.appcki.android.services.action.SUBSCRIBE_AGENDA";

    // TODO: Rename parameters
    public static final String PARAM_AGENDA_ID
            = "nl.uscki.appcki.android.services.extra.PARAM_AGENDA_ID";
    public static final String PARAM_SUBSCRIBE_COMMENT
            = "nl.uscki.appcki.android.services.extra.PARAM_AGENDA_SUBSCRIBE_COMMENT";


    public AgendaSubscriberService() {
        super("AgendaSubscriberService");
    }

    /**
     * Starts this service to perform action subscribe to agenda event action.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startSubscribeAction(Context context, int agendaId) {
        Intent intent = new Intent(context, AgendaSubscriberService.class);
        intent.setAction(ACTION_SUBSCRIBE_AGENDA);
        intent.putExtra(PARAM_AGENDA_ID, agendaId);
        context.startService(intent);
    }

    private CharSequence getSubscribeText(Intent intent) {
        Bundle remoteInput = android.support.v4.app.RemoteInput.getResultsFromIntent(intent);
        if(remoteInput != null) {
            return remoteInput.getCharSequence(PARAM_SUBSCRIBE_COMMENT);
        }
        return "";
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SUBSCRIBE_AGENDA.equals(action)) {
                final int agendaId = intent.getIntExtra(PARAM_AGENDA_ID, -1);
                final String subscribeComment = getSubscribeText(intent).toString();
                Log.e(this.toString(), "Found agenda id " + agendaId + " and comment '" + subscribeComment + "'");
                handleAgendaSubscribeAction(agendaId, subscribeComment, intent);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleAgendaSubscribeAction(final int agendaId, final String subscribeComment, final Intent intent) {
        if(agendaId < 0) {
            error(intent);
            return;
        }

        // Make API available
        ServiceGenerator.init();

        // Get token active
        UserHelper.getInstance().load();

        Services.getInstance()
                .agendaService.subscribe(agendaId, subscribeComment)
                .enqueue(new Callback<AgendaParticipantLists>() {
            @Override
            public void onSucces(Response<AgendaParticipantLists> response) {
                EventBus.getDefault().post(new AgendaItemSubscribedEvent(response.body(), false));
                NotificationReceiver notificationReceiver = new NotificationReceiver();
                boolean allowExport = true;
                if(PermissionHelper.canExportCalendarAuto()) {
                    Log.e(this.toString(), "Can export to calendar, starting intent");
                    allowExport = false;
                    EventExportService.startExportAgendaToCalendarAction(getApplicationContext(), agendaId);
                }
                notificationReceiver.buildNewAgendaItemNotificationFromIntent(intent, allowExport, false);
                Toast.makeText(
                        App.getContext(),
                        getResources().getString(R.string.agenda_subscribe_success),
                        Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void handleError(Response<AgendaParticipantLists> response) {
                super.handleError(response);
                error(intent);
            }
        });
    }

    private void error(Intent intent) {
        NotificationReceiver notificationReceiver = new NotificationReceiver();
        notificationReceiver.buildNewAgendaItemNotificationFromIntent(intent, true, true);
        Toast.makeText(
                App.getContext(),
                getResources().getString(R.string.connection_error),
                Toast.LENGTH_SHORT)
                .show();
    }
}

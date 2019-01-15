package nl.uscki.appcki.android.helpers.notification.agenda;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import com.google.firebase.messaging.RemoteMessage;
import java.io.IOException;
import nl.uscki.appcki.android.R;
import nl.uscki.appcki.android.activities.AgendaActivity;
import nl.uscki.appcki.android.api.ServiceGenerator;
import nl.uscki.appcki.android.api.Services;
import nl.uscki.appcki.android.generated.agenda.AgendaItem;
import nl.uscki.appcki.android.helpers.UserHelper;
import nl.uscki.appcki.android.helpers.calendar.AgendaSubscribeServiceHelper;
import nl.uscki.appcki.android.helpers.calendar.CalendarServiceHelper;
import nl.uscki.appcki.android.helpers.notification.BadWolfNotification;
import nl.uscki.appcki.android.services.AgendaSubscriberIntentService;
import nl.uscki.appcki.android.services.EventExportIntentService;
import retrofit2.Response;

public abstract class ReproducibleAgendaActionNotification extends BadWolfNotification {

    private boolean allowSubscribe = true, allowExport = true;

    public ReproducibleAgendaActionNotification(Context c, RemoteMessage message) {
        super(c, message);
    }

    public ReproducibleAgendaActionNotification(Context c, Intent intent) {
        super(c, intent);
    }

    public ReproducibleAgendaActionNotification(Context c, Intent intent, boolean allowSubscribe, boolean allowExport) {
        super(c, intent);
        this.allowSubscribe = allowSubscribe;
        this.allowExport = allowExport;
    }

    /**
     * Set button behavior
     * Note: the build() method needs to be called before this change is applied to the notification
     * @param allowSubscribe    Whether a subscribe button should be shown
     * @param allowExport       Whether an export button should be shown
     */
    public void setAllowed(boolean allowSubscribe, boolean allowExport) {
        this.allowSubscribe = allowSubscribe;
        this.allowExport = allowExport;
    }

    @Override
    protected Intent getNotificationIntent() {
        Intent intent = new Intent(this.context, AgendaActivity.class);
        intent.putExtra(AgendaActivity.PARAM_AGENDA_ID, this.id);
        return intent;
    }

    @Override
    protected void addActions() {
        if(this.allowSubscribe && checkAllowSimpleSubscribe()) {
            // No need to check this if subscribing from the notification is already enabled anyway
            addSubscribeAction();
        }

        if(this.allowExport) {
            addExportAction();
        }
    }

    private void addExportAction() {
        // Build an export action
        Intent exportAgendaIntent = new Intent(context, EventExportIntentService.class);
        exportAgendaIntent = addReproducabilityExtras(exportAgendaIntent);
        exportAgendaIntent.setAction(CalendarServiceHelper.ACTION_AGENDA_EXPORT);
        exportAgendaIntent.putExtra(CalendarServiceHelper.PARAM_AGENDA_ID, this.id);

        PendingIntent exportAgendapIntent =
                PendingIntent.getService(context, 0, exportAgendaIntent, 0);

        String agenda_export_label = context.getResources().getString(R.string.action_agenda_export);
        this.notificationBuilder.addAction(R.drawable.calendar, agenda_export_label, exportAgendapIntent);
    }

    private void addSubscribeAction() {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            // Operation not supported on older devices
            return;
        }

        // Building remote input object
        String subscribeLabel = this.context.getResources().getString(R.string.action_agenda_subscribe);
        RemoteInput remoteInput = new RemoteInput.Builder(AgendaSubscribeServiceHelper.PARAM_SUBSCRIBE_COMMENT)
                .setLabel(subscribeLabel)
                .setAllowFreeFormInput(true)
                .build();

        // Creating a pending intent for this action
        Intent subscribeIntent = new Intent(this.context, AgendaSubscriberIntentService.class);
        subscribeIntent = addReproducabilityExtras(subscribeIntent);
        subscribeIntent.setAction(AgendaSubscribeServiceHelper.ACTION_SUBSCRIBE_AGENDA);
        subscribeIntent.putExtra(AgendaSubscribeServiceHelper.PARAM_AGENDA_ID, this.id);
        PendingIntent agendaSubscribepIntent = PendingIntent.getService(
                this.context,
                0,
                subscribeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action subscribeAction =
                new NotificationCompat.Action.Builder(R.drawable.plus,
                        subscribeLabel,
                        agendaSubscribepIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        this.notificationBuilder.addAction(subscribeAction);
    }

    private boolean checkAllowSimpleSubscribe() {
        boolean allowSubscribe = true;

        try {
            // Make API available
            ServiceGenerator.init();

            // Get token active
            UserHelper.getInstance().load();

            // Yes, this is blocking, and yes, that's what we want. The service is already on a separate
            // thread, and this way the notification is only shown after we checked what type of agenda
            // item we have
            Response<AgendaItem> agendaResponse = Services.getInstance().agendaService.get(this.id).execute();
            AgendaItem item = agendaResponse.body();

            boolean hasQuestion = item.getQuestion() != null && !item.getQuestion().isEmpty();
            boolean isPrepublished = item.getMaxregistrations() != null && item.getMaxregistrations() == 0;
            boolean isPassedDeadline = item.getHasDeadline() && item.getDeadline().isBeforeNow();
            boolean isPassedStart = !item.getHasDeadline() && item.getStart().isBeforeNow();

            // TODO at some point, just check here if someone is already subscribed. If so, don't show the button any more either

            if(hasQuestion || isPrepublished || isPassedDeadline || isPassedStart) {
                allowSubscribe = false;
            }
        } catch(IOException |NullPointerException e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
            Log.e(getClass().getSimpleName(), "Could not download agenda item with id " + this.id +
                    ". Assuming the worst");
            allowSubscribe = false;
        }
        return allowSubscribe;
    }

}

package nl.uscki.appcki.android.helpers.notification.agenda;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;

import nl.uscki.appcki.android.activities.AgendaActivity;

public class AgendaFromBackupNotification extends ReproducibleAgendaActionNotification {

    public AgendaFromBackupNotification(Context c, RemoteMessage message) {
        super(c, message);
    }

    public AgendaFromBackupNotification(Context c, Intent intent) {
        super(c, intent);
    }

    public AgendaFromBackupNotification(Context c, Intent intent, boolean allowSubscribe, boolean allowExport) {
        super(c, intent, allowSubscribe, allowExport);
    }

    @Override
    protected Intent getNotificationIntent() {
        Intent intent = new Intent(this.context, AgendaActivity.class);
        intent.putExtra(AgendaActivity.PARAM_AGENDA_ID, this.id);
        intent.setAction(AgendaActivity.ACTION_AGENDA_PARTICIPANTS);
        return intent;
    }
}

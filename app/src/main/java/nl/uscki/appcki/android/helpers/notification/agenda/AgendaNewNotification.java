package nl.uscki.appcki.android.helpers.notification.agenda;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;

import nl.uscki.appcki.android.services.NotificationType;

public class AgendaNewNotification extends ReproducibleAgendaActionNotification {

    public AgendaNewNotification(Context c, RemoteMessage message) {
        super(c, message, true, true);
    }

    public AgendaNewNotification(Context c, Intent intent, boolean allowSubscribe, boolean allowExport) {
        super(c, intent, allowSubscribe, allowExport);
    }

    @Override
    protected NotificationType getNotificationType() {
        return NotificationType.agenda_new;
    }
}

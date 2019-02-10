package nl.uscki.appcki.android.helpers.notification.agenda;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;

public class AgendaNewNotification extends ReproducibleAgendaActionNotification {

    public AgendaNewNotification(Context c, RemoteMessage message) {
        super(c, message);
    }

    public AgendaNewNotification(Context c, Intent intent) {
        super(c, intent);
    }

    public AgendaNewNotification(Context c, Intent intent, boolean allowSubscribe, boolean allowExport) {
        super(c, intent, allowSubscribe, allowExport);
    }
}

package nl.uscki.appcki.android.helpers.notification.agenda;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;

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
}

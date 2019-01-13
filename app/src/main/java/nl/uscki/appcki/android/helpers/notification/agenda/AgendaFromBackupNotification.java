package nl.uscki.appcki.android.helpers.notification.agenda;

import android.content.Context;
import com.google.firebase.messaging.RemoteMessage;

public class AgendaFromBackupNotification extends ReproducibleAgendaActionNotification {

    public AgendaFromBackupNotification(Context c, RemoteMessage message) {
        super(c, message, false, true);
    }
}

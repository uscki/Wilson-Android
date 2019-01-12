package nl.uscki.appcki.android.helpers.notification.agenda;

import android.content.Context;
import com.google.firebase.messaging.RemoteMessage;
import nl.uscki.appcki.android.services.NotificationType;

public class AgendaFromBackupNotification extends ReproducibleAgendaActionNotification {

    public AgendaFromBackupNotification(Context c, RemoteMessage message) {
        super(c, message, false, true);
    }

    @Override
    protected NotificationType getNotificationType() {
        return NotificationType.agenda_from_backup;
    }

}

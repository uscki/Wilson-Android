package nl.uscki.appcki.android.helpers.notification.bugtracker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Locale;
import nl.uscki.appcki.android.helpers.notification.BadWolfNotification;

public class BugTrackerNotification extends BadWolfNotification {

    public BugTrackerNotification(Context c, RemoteMessage message) {
        super(c, message);
    }

    @Override
    protected Intent getNotificationIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setData(Uri.parse(String.format(
            Locale.ENGLISH,
            "https://www.uscki.nl/?pagina=Bugtracker/Ticket&id=%d",
            id)
            .trim()));

        return intent;
    }

    @Override
    protected void addActions() {

    }
}

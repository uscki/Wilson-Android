package nl.uscki.appcki.android.helpers.notification.bugtracker;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.firebase.messaging.RemoteMessage;
import java.util.Locale;
import nl.uscki.appcki.android.helpers.notification.BadWolfNotification;
import nl.uscki.appcki.android.services.NotificationType;

public class BugTrackerNotification extends BadWolfNotification {
    public BugTrackerNotification(Context c, RemoteMessage message) {
        super(c, message);
    }

    @Override
    protected NotificationType getNotificationType() {
        return NotificationType.bugtracker_comment;
    }

    @Override
    protected Intent getIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setData(Uri.parse(String.format(
            Locale.ENGLISH,
            "https://www.uscki.nl/?pagina=Bugtracker/Ticket&id=%d",
            id)
            .trim()));

        return intent;
    }

    @Override
    protected String getBackstackAction() {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected String getNotificationCategory() {
        return Notification.CATEGORY_SOCIAL;
    }

    @Override
    protected void addActions() {

    }
}

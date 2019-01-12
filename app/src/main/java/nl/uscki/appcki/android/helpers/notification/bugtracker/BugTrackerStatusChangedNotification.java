package nl.uscki.appcki.android.helpers.notification.bugtracker;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;

public class BugTrackerStatusChangedNotification extends BugTrackerNotification {
    public BugTrackerStatusChangedNotification(Context c, RemoteMessage message) {
        super(c, message);
    }
}

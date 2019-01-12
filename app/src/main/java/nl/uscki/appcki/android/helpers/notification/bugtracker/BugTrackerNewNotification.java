package nl.uscki.appcki.android.helpers.notification.bugtracker;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;

public class BugTrackerNewNotification extends BugTrackerNotification {
    public BugTrackerNewNotification(Context c, RemoteMessage message) {
        super(c, message);
    }
}
